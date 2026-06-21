package com.erp.manufacturing.inventorytransaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionRequest {
    private @NotNull Long itemId;
    private Long warehouseId;
    private Long employeeId;
    private @NotNull @Size(max = 30) String transactionType;
    private @NotNull @Positive BigDecimal quantity;
    private LocalDateTime transactionDate;
    private @Size(max = 255) String remarks;
}
