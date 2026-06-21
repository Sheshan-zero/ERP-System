package com.erp.manufacturing.dashboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Monthly sales amount summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesSummaryDto {
    private String month;
    private BigDecimal totalSalesAmount;
}
