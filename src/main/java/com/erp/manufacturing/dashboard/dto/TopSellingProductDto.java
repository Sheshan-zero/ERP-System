package com.erp.manufacturing.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Top selling finished product summary")
public record TopSellingProductDto(
        Long itemId,
        String itemName,
        BigDecimal totalQuantitySold
) {
}
