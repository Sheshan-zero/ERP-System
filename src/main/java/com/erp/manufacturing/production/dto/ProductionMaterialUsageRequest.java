package com.erp.manufacturing.production.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductionMaterialUsageRequest(
        Long usageId,
        @NotNull Long rawMaterialId,
        @NotNull @Positive BigDecimal quantityUsed,
        LocalDateTime usageDate
) {
}
