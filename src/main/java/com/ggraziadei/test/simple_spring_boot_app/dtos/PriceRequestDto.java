package com.ggraziadei.test.simple_spring_boot_app.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceRequestDto {
    private long productId;
    private long brandId;
    private LocalDateTime date;
}
