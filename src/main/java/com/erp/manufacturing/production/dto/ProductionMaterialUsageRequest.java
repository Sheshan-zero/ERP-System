package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterialUsageRequest {
    private Long usageId;
    private @NotNull Long rawMaterialId;
    private @NotNull @Positive BigDecimal quantityUsed;
    private LocalDateTime usageDate;
}
