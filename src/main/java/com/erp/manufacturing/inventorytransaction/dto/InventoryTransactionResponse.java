package com.erp.manufacturing.inventorytransaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {
    private Long transactionId;
    private Long itemId;
    private String itemName;
    private Long warehouseId;
    private String warehouseName;
    private Long employeeId;
    private String employeeName;
    private String transactionType;
    private BigDecimal quantity;
    private LocalDateTime transactionDate;
    private String remarks;
}
