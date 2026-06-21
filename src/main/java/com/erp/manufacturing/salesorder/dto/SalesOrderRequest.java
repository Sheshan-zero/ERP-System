package com.erp.manufacturing.salesorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.SalesOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderRequest {
    private @NotNull Long customerId;
    private Long employeeId;
    private LocalDateTime orderDate;
    private SalesOrderStatus orderStatus;
    private @Valid List<SalesOrderItemRequest> salesOrderItems;
    private @Valid List<PaymentRequest> payments;
}
