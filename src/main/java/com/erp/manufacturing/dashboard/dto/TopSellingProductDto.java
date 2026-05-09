package com.erp.manufacturing.dashboard.dto;

import java.math.BigDecimal;

public record TopSellingProductDto(
        Long itemId,
        String itemName,
        BigDecimal totalQuantitySold
) {
}
