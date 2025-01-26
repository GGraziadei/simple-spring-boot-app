package com.ggraziadei.test.simple_spring_boot_app.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ggraziadei.test.simple_spring_boot_app.entities.Brand;
import com.ggraziadei.test.simple_spring_boot_app.entities.Price;
import com.ggraziadei.test.simple_spring_boot_app.entities.Product;

@DataJpaTest
public class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 12, 31, 23, 59);
    private static final LocalDateTime REQUEST_DATE = LocalDateTime.of(2020, 9, 14, 10, 0);

    private Product product = Product.builder()
            .name("Product 1")
            .build();

    private Brand brand = Brand.builder()
            .name("Brand 1")
            .build();
            
        
    private Price price1 = Price.builder()
            .product(product)
            .brand(brand)
            .priceList(1)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(35.50)
            .currency("EUR")
            .build();

    private Price price2 = Price.builder()
            .priceList(2)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(25.45)
            .currency("EUR")
            .build();

    private Price price3 = Price.builder()
            .priceList(2)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .price(125.45)
            .currency("EUR")
            .priority(100)
            .build();

    @Test
    public void testSavePrice() {
        Brand savedBrand = brandRepository.save(brand);
        assertNotNull(savedBrand);
        assertEquals(brand, savedBrand);
        System.out.println("Brand saved: " + savedBrand);

        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
        System.out.println("Product saved: " + savedProduct);
        
        price1.setBrand(savedBrand);
        price1.setProduct(savedProduct);

        Price savedPrice = priceRepository.save(price1);
        assertNotNull(savedPrice);
        assertEquals(price1, savedPrice);
        System.out.println("Price saved: " + savedPrice);
    }

    @Test
    public void testFindPriceById() {
        Brand savedBrand = brandRepository.save(brand);
        assertNotNull(savedBrand);
        assertEquals(brand, savedBrand);
        System.out.println("Brand saved: " + savedBrand);

        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
        System.out.println("Product saved: " + savedProduct);
        
        price1.setBrand(savedBrand);
        price1.setProduct(savedProduct);

        Price savedPrice = priceRepository.save(price1);
        assertNotNull(savedPrice);
        assertEquals(price1, savedPrice);
        System.out.println("Price saved: " + savedPrice);

        Price foundPrice = priceRepository.findById(savedPrice.getId()).orElse(null);
        assertNotNull(foundPrice);
        assertEquals(savedPrice, foundPrice);
        System.out.println("Price found: " + foundPrice);
    }

    @Test
    public void testDeletePrice() {
        Brand savedBrand = brandRepository.save(brand);
        assertNotNull(savedBrand);
        assertEquals(brand, savedBrand);
        System.out.println("Brand saved: " + savedBrand);

        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
        System.out.println("Product saved: " + savedProduct);
        
        price1.setBrand(savedBrand);
        price1.setProduct(savedProduct);

        Price savedPrice = priceRepository.save(price1);
        assertNotNull(savedPrice);
        assertEquals(price1, savedPrice);
        System.out.println("Price saved: " + savedPrice);

        priceRepository.delete(savedPrice);
        Price deletedPrice = priceRepository.findById(savedPrice.getId()).orElse(null);
        assertEquals(null, deletedPrice);
        System.out.println("Price deleted: " + deletedPrice);
    }

    @Test
    public void testFindPricePerListPrice(){

        Brand savedBrand = brandRepository.save(brand);
        assertNotNull(savedBrand);
        assertEquals(brand, savedBrand);
        System.out.println("Brand saved: " + savedBrand);

        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
        System.out.println("Product saved: " + savedProduct);
        
        price1.setBrand(savedBrand);
        price1.setProduct(savedProduct);

        Price savedPrice1 = priceRepository.save(price1);
        assertNotNull(savedPrice1);
        assertEquals(price1, savedPrice1);
        System.out.println("Price saved: " + savedPrice1);

        price2.setBrand(savedBrand);
        price2.setProduct(savedProduct);

        Price savedPrice2 = priceRepository.save(price2);
        assertNotNull(savedPrice2);
        assertEquals(price2, savedPrice2);
        System.out.println("Price saved: " + savedPrice2);

        assertEquals(2, priceRepository.findAll().size());
        
        List<Price> prices = priceRepository.findPricePerListPrice(REQUEST_DATE, savedProduct.getId(), savedBrand.getId());
        System.out.println(prices);
        assertEquals(2, prices.size());
        assertTrue(prices.contains(savedPrice1));
        assertTrue(prices.contains(savedPrice2));

        // expect price 3 for price list 2 instead of price 2 for higher priority
        
        price3.setBrand(savedBrand);
        price3.setProduct(savedProduct);
        Price savedPrice3 = priceRepository.save(price3);
        assertNotNull(savedPrice3);
        assertEquals(price3, savedPrice3);
        System.out.println("Price saved: " + savedPrice3);

        assertEquals(3, priceRepository.findAll().size());

        prices = priceRepository.findPricePerListPrice(REQUEST_DATE, savedProduct.getId(), savedBrand.getId());
        System.out.println(prices);
        assertEquals(2, prices.size());
        assertTrue(prices.contains(savedPrice1));
        assertTrue(prices.contains(savedPrice3));
        assertFalse(prices.contains(savedPrice2));
    }

    
}
