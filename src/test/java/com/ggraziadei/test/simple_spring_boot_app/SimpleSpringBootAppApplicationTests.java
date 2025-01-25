package com.ggraziadei.test.simple_spring_boot_app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PriceRequestDto;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PriceResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PricesResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.entities.Price;
import com.ggraziadei.test.simple_spring_boot_app.repositories.PriceRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("e2eTest")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class SimpleSpringBootAppApplicationTests {

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
    private MockMvc mockMvc;

	@Autowired
    private Environment environment;

	@Test
	void testContextLoads() {
		// check loading of the profile
		String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            System.out.println("Active Profile: " + profile);
        }

		List<Price> prices = priceRepository.findAll();
		assertNotNull(prices);
		System.out.println("prices loaded: " + prices);
	}

	
	@ParameterizedTest
	@MethodSource("testCasesProvider")
	void testGetPrices(TestCase testCase) throws Exception {
		PricesResponseDto pricesResponseDto = testCase.getExpectedPricesResponseDto();
		assertNotNull(pricesResponseDto);
		PriceRequestDto priceRequestDto = testCase.getPriceRequestDto();
		assertNotNull(priceRequestDto);
		System.out.println("priceRequestDto: " + priceRequestDto);
		ObjectMapper objectMapper = new ObjectMapper();
		// Register JavaTimeModule to serialize LocalDateTime
		objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
		String expectedPricesResponseJson = objectMapper.writeValueAsString(pricesResponseDto);

		mockMvc.perform(get("/prices")
				.param("brandId", String.valueOf(priceRequestDto.getBrandId()))
				.param("productId", String.valueOf(priceRequestDto.getProductId()))
				.param("date", String.valueOf(priceRequestDto.getDate())))
			.andExpect(status().isOk())
			.andExpect(content().json(expectedPricesResponseJson));
	}
		
	static Stream<TestCase> testCasesProvider() {
		return Stream.of(
			testCase1(), testCase2(), testCase3(), testCase4(), testCase5()
        );
    }

	static TestCase testCase1(){
		LocalDateTime requestDateTime = LocalDateTime.of(2020, 6, 14, 10, 0);
		long productId = 35455L;
		long brandId = 1L;

		HashMap<Integer, PriceResponseDto> pricesTestCase = new HashMap<>();
		PriceResponseDto priceResponseDto1 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(35.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(1)
			.build();
		pricesTestCase.put(1, priceResponseDto1);

		return TestCase.builder()
		.priceRequestDto(PriceRequestDto.builder()
			.brandId(brandId)
			.productId(productId)
			.date(requestDateTime)
			.build())
		.expectedPricesResponseDto(PricesResponseDto.builder()
			.totalPrices(1)
			.requestDateTime(requestDateTime)
			.priceLists(pricesTestCase.keySet())
			.prices(pricesTestCase)
			.build())
		.build();
	}

	static TestCase testCase2(){
		LocalDateTime requestDateTime = LocalDateTime.of(2020, 6, 14, 16, 0);
		long productId = 35455L;
		long brandId = 1L;

		HashMap<Integer, PriceResponseDto> pricesTestCase = new HashMap<>();
		// price list 1
		PriceResponseDto priceResponseDto1 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(35.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(1)
			.build();
		// price list 2
		PriceResponseDto priceResponseDto2 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(25.45)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(2)
			.build();
		pricesTestCase.put(1, priceResponseDto1);
		pricesTestCase.put(2, priceResponseDto2);

		return TestCase.builder()
		.priceRequestDto(PriceRequestDto.builder()
			.brandId(brandId)
			.productId(productId)
			.date(requestDateTime)
			.build())
		.expectedPricesResponseDto(PricesResponseDto.builder()
			.totalPrices(pricesTestCase.size())	
			.requestDateTime(requestDateTime)
			.priceLists(pricesTestCase.keySet())
			.prices(pricesTestCase)
			.build())
		.build();
	}

	static TestCase testCase3(){
		LocalDateTime requestDateTime = LocalDateTime.of(2020, 6, 14, 21, 0);
		long productId = 35455L;
		long brandId = 1L;

		HashMap<Integer, PriceResponseDto> pricesTestCase = new HashMap<>();
		// price list 1
		PriceResponseDto priceResponseDto1 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(35.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(1)
			.build();

		pricesTestCase.put(1, priceResponseDto1);

		return TestCase.builder()
		.priceRequestDto(PriceRequestDto.builder()
			.brandId(brandId)
			.productId(productId)
			.date(requestDateTime)
			.build())
		.expectedPricesResponseDto(PricesResponseDto.builder()
			.totalPrices(pricesTestCase.size())	
			.requestDateTime(requestDateTime)
			.priceLists(pricesTestCase.keySet())
			.prices(pricesTestCase)
			.build())
		.build();
	}

	static TestCase testCase4(){
		LocalDateTime requestDateTime = LocalDateTime.of(2020, 6, 15, 10, 0);
		long productId = 35455L;
		long brandId = 1L;

		HashMap<Integer, PriceResponseDto> pricesTestCase = new HashMap<>();
		// price list 1
		PriceResponseDto priceResponseDto1 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(35.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(1)
			.build();

		// price list 3
		PriceResponseDto priceResponseDto3 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(30.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(3)
			.build();

		pricesTestCase.put(1, priceResponseDto1);
		pricesTestCase.put(3, priceResponseDto3);

		return TestCase.builder()
		.priceRequestDto(PriceRequestDto.builder()
			.brandId(brandId)
			.productId(productId)
			.date(requestDateTime)
			.build())
		.expectedPricesResponseDto(PricesResponseDto.builder()
			.totalPrices(pricesTestCase.size())	
			.requestDateTime(requestDateTime)
			.priceLists(pricesTestCase.keySet())
			.prices(pricesTestCase)
			.build())
		.build();
	}

	static TestCase testCase5(){
		LocalDateTime requestDateTime = LocalDateTime.of(2020, 6, 16, 21, 0);
		long productId = 35455L;
		long brandId = 1L;

		HashMap<Integer, PriceResponseDto> pricesTestCase = new HashMap<>();
		// price list 1
		PriceResponseDto priceResponseDto1 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(35.50)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(1)
			.build();

		// price list 4
		PriceResponseDto priceResponseDto4 = PriceResponseDto.builder()
			.productId(productId)
			.brandId(brandId)
			.price(38.95)
			.currency("EUR")
			.date(requestDateTime)
			.priceList(4)
			.build();
		pricesTestCase.put(1, priceResponseDto1);
		pricesTestCase.put(4, priceResponseDto4);

		return TestCase.builder()
		.priceRequestDto(PriceRequestDto.builder()
			.brandId(brandId)
			.productId(productId)
			.date(requestDateTime)
			.build())
		.expectedPricesResponseDto(PricesResponseDto.builder()
			.totalPrices(pricesTestCase.size())	
			.requestDateTime(requestDateTime)
			.priceLists(pricesTestCase.keySet())
			.prices(pricesTestCase)
			.build())
		.build();
	}

}
