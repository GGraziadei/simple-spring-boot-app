package com.ggraziadei.test.simple_spring_boot_app.dtos;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceResponseDto {
    @Schema(description = "Product identifier", example = "35455")
    private long productId;
    @Schema(description = "Brand identifier", example = "1")
    private long brandId;
    @Schema(description = "Price list identifier", example = "1")
    private long priceList;
    @Schema(description = "Date and time of the request", example = "2020-06-14T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    @Schema(description = "Price", example = "35.50")
    private double price;
    @Schema(description = "Currency", example = "EUR")
    private String currency;
}
