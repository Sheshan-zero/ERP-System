package com.erp.manufacturing.purchaseorder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PurchaseOrderItemRequest(
        @NotNull Long rawMaterialId,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal unitPrice
) {
}
