package com.erp.manufacturing.purchaseorder.dto;

import com.erp.manufacturing.common.enums.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponse(
        Long purchaseOrderId,
        Long supplierId,
        Long employeeId,
        LocalDateTime orderDate,
        LocalDateTime expectedDate,
        PurchaseOrderStatus status,
        BigDecimal totalAmount,
        List<PurchaseOrderItemResponse> purchaseOrderItems
) {
}
