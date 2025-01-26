package com.ggraziadei.test.simple_spring_boot_app.mappers;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.ggraziadei.test.simple_spring_boot_app.dtos.PriceResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.entities.Price;

@Component
public class PriceResponseMapper {
    
    public PriceResponseDto mapToDto(Price entity, LocalDateTime requestDateTime) {
        return PriceResponseDto.builder()
                .productId(entity.getProduct().getId())
                .brandId(entity.getBrand().getId())
                .priceList(entity.getPriceList())
                .date(requestDateTime)
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .build();
    }
}
