package com.erp.manufacturing.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Production order summary statistics")
public record ProductionSummaryDto(
        Long totalOrders,
        Long completedOrders,
        Long plannedOrders,
        BigDecimal totalQuantityProduced
) {
}
