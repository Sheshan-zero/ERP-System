package com.erp.manufacturing.dashboard.dto;

import java.math.BigDecimal;

public record MonthlySalesSummaryDto(
        String month,
        BigDecimal totalSalesAmount
) {
}
