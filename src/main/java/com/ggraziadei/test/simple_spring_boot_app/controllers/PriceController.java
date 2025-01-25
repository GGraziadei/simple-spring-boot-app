package com.ggraziadei.test.simple_spring_boot_app.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.ggraziadei.test.simple_spring_boot_app.dtos.ErrorResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PriceResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.dtos.PricesResponseDto;
import com.ggraziadei.test.simple_spring_boot_app.mappers.PriceResponseMapper;
import com.ggraziadei.test.simple_spring_boot_app.services.PriceService;

import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/prices")
public class PriceController {
    
    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceResponseMapper priceResponseMapper;
    
    @GetMapping
    @Operation(
        summary = "Get prices by product_id, date and brand per price_list", 
        description = "Get a valid price for a product and brand at a given date per price_list",
        tags = {"Prices"},
        method = "GET")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prices found", content = @Content(
            schema = @Schema(implementation = PricesResponseDto.class),
            mediaType = "application/json"
        )),
        @ApiResponse(responseCode = "404", description = "Prices not found", content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class),
            mediaType = "application/json"
        )),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class),
            mediaType = "application/json"
        )),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class),
            mediaType = "application/json"
        )),
    })
    public PricesResponseDto getPrices(
        @RequestParam @NonNull @Positive Long productId, 
        @RequestParam @NonNull @Positive Long brandId,
        @RequestParam @NonNull LocalDateTime date) {
        
        HashMap<Integer, PriceResponseDto> prices = priceService.getPricePerPriceList(productId, brandId, date)
            .entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> priceResponseMapper.mapToDto(entry.getValue(), date),
                (entry1, entry2) -> entry1,
                HashMap::new));    
            
        return PricesResponseDto.builder()
            .prices(prices)
            .totalPrices(prices.size())
            .priceLists(prices.keySet())
            .requestDateTime(date)
            .build();
    }
    
}
