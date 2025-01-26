# Data model
The data domain is the historical data of the prices of a shop.
![UML diagram](/docs/uml_diagram.png)
## Prices
A price is the offer of a brand for a given product and priceList in a date interval (startDate, endDate). 
Given two or more prices available in a date and for a given priceList the offer of the brand is associated to the price with the higher priority. 
### Attributes
- PRODUCT_ID: UUID 
- PRICE_LIST: integer Define the  applicable price list. The author considers this attribute as an indicator of the catalog on which the price is applicable (a.k.a. lista de precios), thus it is the scope of the price. In a real example two price lists could be  *b2b* and *b2c*.
- PRODUCT_ID: long
- BRAND_ID: long
- START_DATE: dateTime Define the starting datetime for the price validity
- END_DATE: dateTime Define the ending datetime for the price validity
- PRICE: double
- CURRENCY: string
- PRIORITY: integer Described by the integrity constraint

### Constrains
- Integrity constraint for the historical data:
Given two available prices for a product and brand in a given date, thus START_DATE <= date <= END_DATE, if they are related to the same PRICE_LIST, they have different priorities. 

### Indexes
Over the assumptions (hypothesis introduced by the author) that:
- the granularity for a price update is one minute
- there are *P > 10^3* products and *B > 10^ 2* brands in the database 

The PRICES table contains more than *P X B X deltaTime > 10^5 * deltaTime* rows, thus it is required the introduction of additional physical mean to facilitate the data manipulation on db.

- idx_product_id: treeIndex on the PRODUCT_ID attribute with a factor of reduction *P*
- idx_brand_id: treeIndex on the PRODUCT_ID attribute with a factor of reduction *B*
- idx_date_interval: composite index on the START_DATE and END_DATE with a factor of deduction *deltaTime* 
- idx_priority: treeIndex on PRIORITY. It is not clear the granularity of the priority attribute, maybe it is not really required. To understand if it is possible to remove it, it is required an heuristic analysis on real data. 

### Queries 
Retrieve the available prices for a BRAND_ID and PRODUCT_ID valid in a given date (START_DATE <= date <= END_DATE) per each PRICE_LIST. In case of more than one valid price available for a PRICE_LIST, select the one with the highest priority. 

```
SELECT 
    CURRENCY, 
    PRICE, 
    PRICE_LIST, 
    PRIORITY, 
    BRAND_ID, 
    END_DATE, 
    PRODUCT_ID, 
    START_DATE, 
    PRICE_ID 
FROM (
    SELECT 
        CURRENCY, 
        PRICE, 
        PRICE_LIST, 
        PRIORITY, 
        BRAND_ID, 
        END_DATE, 
        PRODUCT_ID, 
        START_DATE, 
        PRICE_ID, 
        ROW_NUMBER() OVER (
            PARTITION BY PRODUCT_ID, BRAND_ID, PRICE_LIST 
            ORDER BY PRIORITY DESC
        ) AS RN
    FROM PRICES
    WHERE 
        BRAND_ID = ? 
        AND PRODUCT_ID = ? 
        AND START_DATE <= ? 
        AND END_DATE >= ?
) AS subquery
WHERE RN = 1;
```

The solution defines a partition with 
`PARTITION BY PRODUCT_ID, BRAND_ID, PRICE_LIST`
for each partition sorts the row according to `PRIORITY DESC` and assigns a row number `RN` (valid for that partition). For each partition related to a `PRICE_LIST` the row with `RN=1` is the price with highest priority, thus valid selected one for that catalog.

This query is the best performance solution for the following reasons:
- Using the indexes at most `PRICE_LIST` prices are accessed in memory
- The access time to disk is the most impactful cause for the performance degradation. 
- The post-processing time for the spring-application is proportional with the number of retrieved rows.

# Spring boot application 
The application follows an hexagonal architecture adapted to the small size of the project.
- domain layer:
    - Entities
- infrastructure layer:
    - Repositories 
- application layer:
    - Services
    - Mappers 
- external interface layer:
    - Controllers 

![architecture](/docs/architecture.png)

## Entities
Core concept of the application.
They are annotated with @Entity. 
Given the simplicity of this application each one is mapped to one table of the data model and the only required core functions are getters and setters. There are not computed attributes. 

## Repositories
Manage the storage and retrieval of domain entities (CRUD ops) hiding implementation details, thus are outbound adapters

### PriceRepository
Interface annotated with @Repository which extends the JpaRepository interface which provides all the CRUD operations.

The repository maps the custom native query (placed in the field {QUERY}) described in the data model section with the following function signature annotation. As adapter, the repository returns a List<> of entities defined in the domain model. 

```
@Query(value = {QUERY} ,nativeQuery = true)
List<Price> findPricePerListPrice(
                    @Param("date") LocalDateTime date, 
                    @Param("productId") Long productId, 
                    @Param("brandId") Long brandId);
```
## Services
In this simple application I have only one application service as the intermediary between the interface layer (controllers) and the domain layer. The business logic is delegated to the domain layer. 
### PriceService
Application service, thus annotated with @Service, with one internal API. 
```
/**
    * Get prices by product_id, date and brand per price_list
    * If there are no prices, return an empty map
    * If the product or brand does not exist, return an empty map
    * 
    * @param productId Product identifier
    * @param brandId Brand identifier
    * @param date Date and time of the request
    * @return HashMap<Integer, Price> Prices per price list identifier
*/
public HashMap<Integer, Price> getPricePerPriceList(long productId, long brandId, LocalDateTime date)
```

## Mappers
Handle data transformations between different layers of the application. In the scope of this application limited to the conversion of DTOs (used by the interface layer) into domain entities (used by the domain layer) and vice versa.
### PriceResponseMapper
Maps the Price entity to PriceReponseDto. For the scope of the application the vice-versa was not required. It is annotated as @Component. 
```
public PriceResponseDto mapToDto(Price entity, LocalDateTime requestDateTime);
```
## Controllers
Serve as the entry point for external interactions (a.k.a inbound adapter), transform user input into DTOs passed to the application layer. Return the output to the client into DTOs. 
### PriceController
Transforms the user input into a PriceRequestDTO, which is passed to the application service PriceService. *Given the simplicity of the request it is not really explicitly encoded into the dto. Processes the response of the application service and calling the PriceReponseMapper retrieve the PriceResponseDTO. 
Than it aggregates the PriceResponseDTO into PricesResponseDTO which contains additional meta data and structure to serve the output to the client. 
It is annotated as @Controller and registers responsible for the endpoint /prices*. 

#### Registered endpoints 
`GET /api/v1/prices `

Go to Open API section to retrieve the details.

### GlobalExceptionController
Handles the exceptions and returns an ErrorResponseDto to the client. 

## Open API 
The application exposes the following Open API. 
The interactive documentation with the SwaggerUI is available running the SpringBoot application at http://localhost:8080/api/v1/docs

```
openapi: 3.1.0
servers:
  - url: http://localhost:8080/api/v1
paths:
  /prices:
    get:
      tags:
        - Prices
      summary: Get prices by product_id, date and brand per price_list
      description: Get a valid price for a product and brand at a given date per price_list
      operationId: getPrices
      parameters:
        - name: productId
          in: query
          required: true
          schema:
            type: integer
            format: int64
        - name: brandId
          in: query
          required: true
          schema:
            type: integer
            format: int64
        - name: date
          in: query
          required: true
          schema:
            type: string
            format: date-time
      responses:
        '200':
          description: Prices found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PricesResponseDto'
        '500':
          description: Internal server error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
        '404':
          description: Prices not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
        '400':
          description: Bad request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
components:
  schemas:
    PriceResponseDto:
      type: object
      properties:
        productId:
          type: integer
          format: int64
          description: Product identifier
          example: 35455
        brandId:
          type: integer
          format: int64
          description: Brand identifier
          example: 1
        priceList:
          type: integer
          format: int64
          description: Price list identifier
          example: 1
        date:
          type: string
          format: date-time
          description: Date and time of the request
          example: "2020-06-14T10:00:00"
        price:
          type: number
          format: double
          description: Price
          example: 35.5
        currency:
          type: string
          description: Currency
          example: EUR
    PricesResponseDto:
      type: object
      properties:
        prices:
          type: object
          additionalProperties:
            $ref: '#/components/schemas/PriceResponseDto'
          description: Prices per price list
        totalPrices:
          type: integer
          format: int32
        priceLists:
          type: array
          items:
            type: integer
            format: int32
          uniqueItems: true
        requestDateTime:
          type: string
          format: date-time
    ErrorResponseDto:
      type: object
      properties:
        message:
          type: string
        details:
          type: string
```
### Examples
The database with pre-loaded rows is the default one in the `application.properties`.

Request

```
curl -X 'GET' \
  'http://localhost:8080/api/v1/prices?productId=1&brandId=1&date=2020-10-10T10%3A00%3A00' \
  -H 'accept: application/json'
```

Response 
```
{
  "prices": {
    "1": {
      "productId": 1,
      "brandId": 1,
      "priceList": 1,
      "date": "2020-10-10T10:00:00",
      "price": 25.5,
      "currency": "EUR"
    },
    "2": {
      "productId": 1,
      "brandId": 1,
      "priceList": 2,
      "date": "2020-10-10T10:00:00",
      "price": 25.5,
      "currency": "EUR"
    }
  },
  "totalPrices": 2,
  "priceLists": [
    1,
    2
  ],
  "requestDateTime": "2020-10-10T10:00:00"
}
```

# Tests
## Unit tests
### PriceControllerTest
Tests the price controller mocking the sub-layers using mockito. 
In particular the price service is mocked and it is possible with this code:
```
@MockitoBean
private PriceService priceService;

private HashMap<Integer, Price> prices = new HashMap<>();

@BeforeEach
public void setUp() {
    prices.clear();
    
    [creating mocking data]
    
    prices.put(1, price1);
    prices.put(2, price2);

    when(priceService.getPricePerPriceList(anyLong(), anyLong(), any(LocalDateTime.class)))
        .thenReturn(prices);
}
```
performed tests:
- testMissingArguments: checks the expected ErrorResponseDto in case of missing arguments
- testValidationArguments: check the expected ErrorResponseDto in case of invalid arguments (e.g. no positive id) WIP
- testGetPrices: checks the 200 expected response (PricesResponseDto) in case of all good input parameters. Checks the additional meta data and structure served to client and computed by teh controller. 
This class is annotated with @SpringBootTest. 

### PriceRepositoryTest
Tests the PriceRepository interface implementation. 
With the annotation @DataJpaTest activates a different profile, thus the tests are not performed on the h2 default database.

- testSavePrice, testDeletePrice: checks data model integrity adding a product, a brand and a price in db and removing it.
- testFindPriceById: checks the selection of a price given its id. 
- testFindPricePerListPrice: checks the correct execution of the custom query. It provides the following cases:
    - Given two active prices p1, p2 for the same product and brand but with two different priceList return p1,p2 (thus one per price list). 
    - Given active prices p1,p2 and an additional price p3 with the same price list of p2 but higher priority, returns p1 and p3 (thus one per price list with highest priority). 

### PriceServiceTest
Unit test for the service annotated with @SpringBootTest.

## Integration tests
### SimpleSpringBootAppApplicationTests
The are performed e2e integrations tests. 
The class is annotated with @SpringBootTest and @ActiveProfile("e2eTest"). The custom profile is defined in `application-e2eTest.properties` to create an empty database from scratch each time that the class is instantiated and 
the annotation `@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)` loads the test env in the database before the instantiation of the class. 

The executed test is the e2e get prices, the only mocked part is the client request. 
The function `testGetPrices` is a parametrized test that receives as parameter a `TestCase` instance with the request and the hard-coded expected response. The test check that the output is equal to the expected one. 
```
@ParameterizedTest
@MethodSource("testCasesProvider")
void testGetPrices(TestCase testCase) throws Exception
```
The method testCasesProvider returns a Stream of testCase. 
```
static Stream<TestCase> testCasesProvider() {
    return Stream.of(
        testCase1(), testCase2(), [...]
    );
}
```
####  Prices test table

Populated price table.

| price_id         | brand_id | start_date           | end_date             | price_list | product_id | priority | price | currency |
|------------------|----------|----------------------|----------------------|------------|------------|----------|-------|----------|
| 1   | 1        | 2020-06-14 00:00:00  | 2020-12-31 23:59:59  | 1          | 35455      | 0        | 35.50 | EUR      |
| 2    | 1        | 2020-06-14 15:00:00  | 2020-06-14 18:30:00  | 2          | 35455      | 1        | 25.45 | EUR      |
| 3    | 1        | 2020-06-15 00:00:00  | 2020-06-15 11:00:00  | 3          | 35455      | 1        | 30.50 | EUR      |
| 4    | 1        | 2020-06-15 16:00:00  | 2020-12-31 23:59:59  | 4          | 35455      | 1        | 38.95 | EUR      |
| 5    | 1        | 2020-06-14 15:00:00  | 2020-06-14 18:30:00  | 2          | 35455      | 0        | 25.45 | EUR      |
| 6    | 1        | 2020-06-15 00:00:00  | 2020-06-15 11:00:00  | 3          | 35455      | 0        | 30.50 | EUR      |
| 7    | 1        | 2020-06-15 16:00:00  | 2020-12-31 23:59:59  | 4          | 35455      | 0        | 38.95 | EUR      |

Row with id 5,6,7 are added to check the highest priority rule per price list. 

### Test cases

| Test Number | Request Time      | Request Date  | Product ID | Brand ID | Brand Name | Expected prices id in output |
|-------------|-------------------|---------------|------------|----------|------------|------------|
| Test 1      | 10:00            | 14th June     | 35455      | 1        | ZARA       | 1 |
| Test 2      | 16:00            | 14th June     | 35455      | 1        | ZARA       | 1,2 | 
| Test 3      | 21:00            | 14th June     | 35455      | 1        | ZARA       | 1 |
| Test 4      | 10:00            | 15th June     | 35455      | 1        | ZARA       | 1,3 | 
| Test 5      | 21:00            | 16th June     | 35455      | 1        | ZARA       | 1,4 |


