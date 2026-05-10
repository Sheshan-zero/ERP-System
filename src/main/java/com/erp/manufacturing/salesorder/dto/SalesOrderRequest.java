package com.erp.manufacturing.salesorder.dto;

import com.erp.manufacturing.common.enums.SalesOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderRequest(
        @NotNull Long customerId,
        Long employeeId,
        LocalDateTime orderDate,
        SalesOrderStatus orderStatus,
        @Valid List<SalesOrderItemRequest> salesOrderItems,
        @Valid List<PaymentRequest> payments
) {
}
