package com.erp.manufacturing.purchaseorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseBillDto {
    private Long billNumber;
    private Long purchaseOrderId;
    private Long supplierId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
}
