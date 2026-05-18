package com.erp.manufacturing.salesorder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesInvoiceDto(
        Long invoiceNumber,
        Long salesOrderId,
        Long customerId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal balanceAmount,
        String status
) {
}
