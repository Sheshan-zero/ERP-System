package com.erp.manufacturing.inventorytransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
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