package com.erp.manufacturing.billofmaterial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterialRequest {
    private @NotNull Long finishedProductId;
    private @NotNull Long rawMaterialId;
    private @NotNull @Positive BigDecimal requiredQuantity;
    private @PositiveOrZero BigDecimal wastagePercentage;
}
