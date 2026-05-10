package com.erp.manufacturing.billofmaterial.dto;

import java.math.BigDecimal;

public record BillOfMaterialResponse(
        Long bomId,
        Long finishedProductId,
        String finishedProductName,
        Long rawMaterialId,
        String rawMaterialName,
        BigDecimal requiredQuantity,
        BigDecimal wastagePercentage
) {
}
