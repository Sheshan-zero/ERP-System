package com.erp.manufacturing.dashboard.dto;

import java.math.BigDecimal;

public record ProductionSummaryDto(
        Long totalOrders,
        Long completedOrders,
        Long plannedOrders,
        BigDecimal totalQuantityProduced
) {
}
