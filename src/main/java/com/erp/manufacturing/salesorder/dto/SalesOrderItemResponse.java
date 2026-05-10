package com.erp.manufacturing.salesorder.dto;

import java.math.BigDecimal;

public record SalesOrderItemResponse(
        Long salesOrderItemId,
        Long finishedProductId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
