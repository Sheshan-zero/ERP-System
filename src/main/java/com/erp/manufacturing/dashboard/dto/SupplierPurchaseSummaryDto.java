package com.erp.manufacturing.dashboard.dto;

import java.math.BigDecimal;

public record SupplierPurchaseSummaryDto(
        Long supplierId,
        String supplierName,
        BigDecimal totalPurchaseAmount
) {
}
