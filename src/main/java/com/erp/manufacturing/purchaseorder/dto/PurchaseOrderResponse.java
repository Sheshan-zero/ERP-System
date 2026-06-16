package com.erp.manufacturing.purchaseorder.dto;

import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
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