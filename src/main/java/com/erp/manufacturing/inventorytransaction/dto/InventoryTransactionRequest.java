package com.erp.manufacturing.inventorytransaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryTransactionRequest(
        @NotNull Long itemId,
        Long warehouseId,
        Long employeeId,
        @NotNull @Size(max = 30) String transactionType,
        @NotNull @Positive BigDecimal quantity,
        LocalDateTime transactionDate,
        @Size(max = 255) String remarks
) {
}
