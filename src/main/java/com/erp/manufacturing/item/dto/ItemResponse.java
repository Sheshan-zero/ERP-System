package com.erp.manufacturing.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemResponse(
        Long itemId,
        String itemName,
        String itemType,
        String unitOfMeasure,
        BigDecimal currentStock,
        BigDecimal reorderLevel,
        String itemStatus,
        String description,
        LocalDateTime createdDate,
        Long version
) {
}
