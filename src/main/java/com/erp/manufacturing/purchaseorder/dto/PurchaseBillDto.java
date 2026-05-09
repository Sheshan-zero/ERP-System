package com.erp.manufacturing.purchaseorder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseBillDto(
        Long billNumber,
        Long purchaseOrderId,
        Long supplierId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status
) {
}
