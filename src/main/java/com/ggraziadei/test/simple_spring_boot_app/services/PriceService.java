package com.ggraziadei.test.simple_spring_boot_app.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.ggraziadei.test.simple_spring_boot_app.entities.Price;
import com.ggraziadei.test.simple_spring_boot_app.repositories.PriceRepository;

@Service
public class PriceService implements DAOPattern<Price, String> {

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public Price save(Price entity) {
        UUID uuid = UUID.randomUUID();
        entity.setId(uuid.toString());
        return priceRepository.save(entity);
    }

    @Override
    public Price findById(String id) {
        return priceRepository.findById(id).orElse(null);
    }

    @Override
    public Price update(Price entity) {
        return priceRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        priceRepository.deleteById(id);
    }

    /**
     * Get prices by product_id, date and brand per price_list
     * With one query, get all prices for a product and brand at a given date
     * If there are no prices, return an empty map
     * If the product or brand does not exist, return an empty map
     * 
     * @param productId Product identifier
     * @param brandId Brand identifier
     * @param date Date and time of the request
     * @return HashMap<Integer, Price> Prices per price list identifier
    */
    public HashMap<Integer, Price> getPricePerPriceList(long productId, long brandId, LocalDateTime date) {
        List<Price> prices = priceRepository.findPricePerListPrice(date, productId, brandId);
        return prices.stream()
            .collect(Collectors.toMap(Price::getPriceList,
                price -> price, // price is the value
                (price1, price2) -> price1, // collisions are not expected, the query should return only one price per price list
                HashMap::new));
    }
    
}
