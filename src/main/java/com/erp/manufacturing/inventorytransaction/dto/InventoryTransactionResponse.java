package com.erp.manufacturing.inventorytransaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryTransactionResponse(
        Long transactionId,
        Long itemId,
        String itemName,
        Long warehouseId,
        String warehouseName,
        Long employeeId,
        String employeeName,
        String transactionType,
        BigDecimal quantity,
        LocalDateTime transactionDate,
        String remarks
) {
}
