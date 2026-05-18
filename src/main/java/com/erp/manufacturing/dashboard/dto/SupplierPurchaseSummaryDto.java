package com.erp.manufacturing.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Supplier purchase amount summary")
public record SupplierPurchaseSummaryDto(
        Long supplierId,
        String supplierName,
        BigDecimal totalPurchaseAmount
) {
}
