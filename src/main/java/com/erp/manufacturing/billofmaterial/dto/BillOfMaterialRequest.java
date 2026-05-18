package com.erp.manufacturing.billofmaterial.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BillOfMaterialRequest(
        @NotNull Long finishedProductId,
        @NotNull Long rawMaterialId,
        @NotNull @Positive BigDecimal requiredQuantity,
        @PositiveOrZero BigDecimal wastagePercentage
) {
}
