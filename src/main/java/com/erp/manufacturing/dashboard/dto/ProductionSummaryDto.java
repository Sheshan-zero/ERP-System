package com.erp.manufacturing.dashboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Production order summary statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionSummaryDto {
    private Long totalOrders;
    private Long completedOrders;
    private Long plannedOrders;
    private BigDecimal totalQuantityProduced;
}
