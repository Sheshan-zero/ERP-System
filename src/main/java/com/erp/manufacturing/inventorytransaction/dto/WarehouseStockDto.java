package com.erp.manufacturing.inventorytransaction.dto;

import java.math.BigDecimal;

public record WarehouseStockDto(
        Long itemId,
        String itemName,
        Long warehouseId,
        String warehouseName,
        BigDecimal quantityOnHand
) {
}
