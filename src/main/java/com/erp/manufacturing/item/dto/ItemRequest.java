package com.erp.manufacturing.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemRequest(
        Long itemId,
        @NotBlank @Size(max = 100) String itemName,
        @NotBlank @Size(max = 30) String itemType,
        @NotBlank @Size(max = 20) String unitOfMeasure,
        @DecimalMin("0.00") BigDecimal currentStock,
        @DecimalMin("0.00") BigDecimal reorderLevel,
        @Size(max = 20) String itemStatus,
        @Size(max = 255) String description,
        LocalDateTime createdDate
) {
}
