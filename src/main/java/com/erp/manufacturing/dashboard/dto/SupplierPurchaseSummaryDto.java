package com.erp.manufacturing.dashboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Supplier purchase amount summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPurchaseSummaryDto {
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalPurchaseAmount;
}
