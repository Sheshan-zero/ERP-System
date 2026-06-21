package com.erp.manufacturing.purchaseorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {
    private @NotNull Long supplierId;
    private Long employeeId;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;
    private PurchaseOrderStatus status;
    private @Valid List<PurchaseOrderItemRequest> purchaseOrderItems;
}
