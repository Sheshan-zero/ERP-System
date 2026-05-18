package com.erp.manufacturing.salesorder.dto;

import com.erp.manufacturing.common.enums.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderResponse(
        Long salesOrderId,
        Long customerId,
        Long employeeId,
        LocalDateTime orderDate,
        SalesOrderStatus orderStatus,
        BigDecimal totalAmount,
        List<SalesOrderItemResponse> salesOrderItems,
        List<PaymentResponse> payments
) {
}
