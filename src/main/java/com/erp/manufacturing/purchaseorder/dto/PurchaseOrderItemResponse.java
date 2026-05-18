package com.erp.manufacturing.purchaseorder.dto;

import java.math.BigDecimal;

public record PurchaseOrderItemResponse(
        Long purchaseOrderItemId,
        Long rawMaterialId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
