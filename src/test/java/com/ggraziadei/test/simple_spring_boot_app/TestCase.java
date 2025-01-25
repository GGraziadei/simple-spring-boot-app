package com.ggraziadei.test.simple_spring_boot_app;

import com.ggraziadei.test.simple_spring_boot_app.dtos.PriceRequestDto;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PricesResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestCase {
    private PriceRequestDto priceRequestDto;
    private PricesResponseDto expectedPricesResponseDto;
}
