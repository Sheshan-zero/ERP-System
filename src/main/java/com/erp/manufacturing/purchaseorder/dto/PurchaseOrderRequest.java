package com.erp.manufacturing.purchaseorder.dto;

import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderRequest(
        @NotNull Long supplierId,
        Long employeeId,
        LocalDateTime orderDate,
        LocalDateTime expectedDate,
        PurchaseOrderStatus status,
        @Valid List<PurchaseOrderItemRequest> purchaseOrderItems
) {
}
