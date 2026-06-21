package com.erp.manufacturing.salesorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesInvoiceDto {
    private Long invoiceNumber;
    private Long salesOrderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String status;
}
