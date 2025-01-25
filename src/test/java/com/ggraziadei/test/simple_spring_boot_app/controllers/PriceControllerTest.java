package com.ggraziadei.test.simple_spring_boot_app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggraziadei.test.simple_spring_boot_app.entities.Brand;
import com.ggraziadei.test.simple_spring_boot_app.entities.Price;
import com.ggraziadei.test.simple_spring_boot_app.entities.Product;
import com.ggraziadei.test.simple_spring_boot_app.services.PriceService;

@SpringBootTest
@AutoConfigureMockMvc
public class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceService priceService;

    private HashMap<Integer, Price> prices = new HashMap<>();
    private Price price1;
    private Price price2;
    private Brand brand;
    private Product product;

    private static final long PRODUCT_ID = 1L;
    private static final long BRAND_ID = 1L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 12, 31, 23, 59);
    private static final LocalDateTime REQUEST_DATE = LocalDateTime.of(2020, 9, 14, 10, 0);

    @BeforeEach
    public void setUp() {
        prices.clear();
        
        product = Product.builder()
            .id(PRODUCT_ID)
            .name("Product 1")
            .build();

        brand = Brand.builder()
            .id(BRAND_ID)
            .name("Brand 1")
            .build();
            
        
        price1 = Price.builder()
            .id("id-1")
            .product(product)
            .brand(brand)
            .priceList(1)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(35.50)
            .currency("EUR")
            .build();

        price2 = Price.builder()
            .id("id-2")
            .product(product)
            .brand(brand)
            .priceList(2)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(25.45)
            .currency("EUR")
            .build();

        prices.put(1, price1);
        prices.put(2, price2);

        when(priceService.getPricePerPriceList(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenReturn(prices);
    }

    @Test
    public void testGetPrices() {
        try {
            String pricesSerialized = new ObjectMapper().writeValueAsString(prices);

            mockMvc.perform(
                get("/prices")
                    .param("productId", String.valueOf(PRODUCT_ID))
                    .param("brandId", String.valueOf(BRAND_ID))
                    .param("date", REQUEST_DATE.toString()))
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.prices").exists())
            .andExpect(jsonPath("$.prices").isMap())
            .andExpect(jsonPath("$.prices").value(pricesSerialized))
            .andExpect(jsonPath("$.totalPrices").value(prices.size()))
            .andExpect(jsonPath("$.priceLists").isArray())
            .andExpect(jsonPath("$.priceLists[0]").value(price1.getPriceList()))
            .andExpect(jsonPath("$.priceLists[1]").value(price2.getPriceList()));
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
    }

    @Test
    public void testMissingArguments(){
        // Missing brandId
        try {
            mockMvc.perform(
                get("/prices")
                    .param("productId", String.valueOf(PRODUCT_ID))
                    .param("date", REQUEST_DATE.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("Required request parameter 'brandId' for method parameter type Long is not present"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Missing productId
        try {
            mockMvc.perform(
                get("/prices")
                    .param("brandId", String.valueOf(BRAND_ID))
                    .param("date", REQUEST_DATE.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("Required request parameter 'productId' for method parameter type Long is not present"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Missing date
        try {
            mockMvc.perform(
                get("/prices")
                    .param("productId", String.valueOf(PRODUCT_ID))
                    .param("brandId", String.valueOf(BRAND_ID)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("Required request parameter 'date' for method parameter type LocalDateTime is not present"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testValidationArguments(){
        // Bad format brandId
        try {
            mockMvc.perform(
                get("/prices")
                    .param("productId", String.valueOf(PRODUCT_ID))
                    .param("brandId", "-1")
                    .param("date", REQUEST_DATE.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("400 BAD_REQUEST \"Validation failure\""));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
}
