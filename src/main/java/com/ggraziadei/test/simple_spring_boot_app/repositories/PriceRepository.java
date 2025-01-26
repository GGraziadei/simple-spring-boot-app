package com.ggraziadei.test.simple_spring_boot_app.repositories;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ggraziadei.test.simple_spring_boot_app.entities.Price;

public interface PriceRepository extends JpaRepository<Price, String> {

    @Query(value = "SELECT " +
            "   CURRENCY, PRICE, PRICE_LIST, PRIORITY, BRAND_ID, END_DATE, PRODUCT_ID, START_DATE, PRICE_ID " +
            "FROM (" +
            "   SELECT CURRENCY, PRICE, PRICE_LIST, PRIORITY, BRAND_ID, END_DATE, PRODUCT_ID, START_DATE, PRICE_ID," +
            "   ROW_NUMBER() OVER( PARTITION BY PRODUCT_ID, BRAND_ID, PRICE_LIST ORDER BY PRIORITY DESC) as RN " +
            "   FROM PRICES " +
            "   WHERE BRAND_ID = :brandId " + 
            "     AND PRODUCT_ID = :productId " +
            "     AND START_DATE <= :date " +
            "     AND END_DATE >= :date " +
            ") AS subquery " +
            "WHERE RN = 1", nativeQuery = true)
    List<Price> findPricePerListPrice(@Param("date") LocalDateTime date, 
                        @Param("productId") Long productId, 
                        @Param("brandId") Long brandId);

    
}
