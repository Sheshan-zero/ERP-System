package com.erp.manufacturing.warehouse.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
    private Long warehouseId;
    private String warehouseName;
    private String location;
    private BigDecimal capacity;
    private String managerName;
}
