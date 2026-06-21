package com.erp.manufacturing.purchaseorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    private Long purchaseOrderId;
    private Long supplierId;
    private Long employeeId;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemResponse> purchaseOrderItems;
}
