package com.ggraziadei.test.simple_spring_boot_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ggraziadei.test.simple_spring_boot_app.entities.Brand;
import com.ggraziadei.test.simple_spring_boot_app.entities.Price;
import com.ggraziadei.test.simple_spring_boot_app.entities.Product;
import com.ggraziadei.test.simple_spring_boot_app.repositories.PriceRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PriceServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceRepository priceRepository;

    @Autowired
    private PriceService priceService;

    private static final long PRODUCT_ID = 1L;
    private static final long BRAND_ID = 1L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 12, 31, 23, 59);
    private final List<Price> prices = new ArrayList<>();
    
    @BeforeEach
    public void setUp() {
        prices.clear();
        
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("Product 1")
            .build();

        Brand brand = Brand.builder()
            .id(BRAND_ID)
            .name("Brand 1")
            .build();
            
        
        Price price1 = Price.builder()
            .id("id-1")
            .product(product)
            .brand(brand)
            .priceList(1)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(35.50)
            .currency("EUR")
            .build();

        Price price2 = Price.builder()
            .id("id-2")
            .product(product)
            .brand(brand)
            .priceList(2)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(25.45)
            .currency("EUR")
            .build();

        prices.add(price1);
        prices.add(price2);

        when(priceRepository.findPricePerListPrice(any(LocalDateTime.class), anyLong(), anyLong()))
            .thenReturn(prices);

        System.out.println("PriceServiceTest.setUp() - prices: " + priceRepository.findPricePerListPrice(
            LocalDateTime.of(2025, 1, 25, 0, 0, 0), 1L, 1L));
    }

    @Test
    public void testGetPricePerPriceList() {
        Map<Integer, Price> result = priceService.getPricePerPriceList(PRODUCT_ID, BRAND_ID, LocalDateTime.of(2025, 1, 25, 0, 0, 0));
        
        result.entrySet().stream()
            .forEach(entry -> {
                // computing integrity of the result
                int priceList = entry.getKey();
                Price price = entry.getValue();
                assertNotNull(price);
                assertEquals(priceList, price.getPriceList());
                
                // integrity of the output with the input 
                String priceId = price.getId();
                Price expectedPrice = prices.stream()
                    .filter(p -> p.getId().equals(priceId))
                    .findFirst()
                    .orElse(null);
                assertNotNull(expectedPrice);
                assertEquals(expectedPrice.getPrice(), price.getPrice());
                assertEquals(expectedPrice.getCurrency(), price.getCurrency());
                assertEquals(expectedPrice.getPriceList(), priceList);
                assertEquals(expectedPrice.getBrand(), price.getBrand());
            });
    }
    
}
