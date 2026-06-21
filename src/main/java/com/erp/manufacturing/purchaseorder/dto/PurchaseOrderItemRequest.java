package com.erp.manufacturing.purchaseorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemRequest {
    private Long purchaseOrderItemId;
    private @NotNull Long rawMaterialId;
    private @NotNull @Positive BigDecimal quantity;
    private @NotNull @Positive BigDecimal unitPrice;
}
