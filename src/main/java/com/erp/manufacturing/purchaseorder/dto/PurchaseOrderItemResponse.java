package com.erp.manufacturing.purchaseorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {

    private Long purchaseOrderItemId;

    private Long rawMaterialId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}