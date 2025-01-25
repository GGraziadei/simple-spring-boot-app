package com.ggraziadei.test.simple_spring_boot_app.dtos;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PricesResponseDto {
    @Schema(description = "Prices per price list")
    private HashMap<Integer, PriceResponseDto> prices;
    private int totalPrices;
    private Set<Integer> priceLists;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestDateTime;
}
