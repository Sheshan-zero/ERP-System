package com.erp.manufacturing.salesorder.dto;

import com.erp.manufacturing.common.enums.SalesOrderStatus;
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