package com.erp.manufacturing.warehouse.dto;

import java.math.BigDecimal;

public record WarehouseResponse(
        Long warehouseId,
        String warehouseName,
        String location,
        BigDecimal capacity,
        String managerName
) {
}
