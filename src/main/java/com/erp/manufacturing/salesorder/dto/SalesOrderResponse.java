package com.erp.manufacturing.salesorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {
    private Long salesOrderId;
    private Long customerId;
    private Long employeeId;
    private LocalDateTime orderDate;
    private SalesOrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<SalesOrderItemResponse> salesOrderItems;
    private List<PaymentResponse> payments;
}
