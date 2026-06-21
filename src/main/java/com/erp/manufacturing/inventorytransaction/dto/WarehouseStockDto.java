package com.erp.manufacturing.inventorytransaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStockDto {
    private Long itemId;
    private String itemName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal quantityOnHand;
}
