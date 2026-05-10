package com.erp.manufacturing.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WarehouseRequest(
        @NotBlank @Size(max = 100) String warehouseName,
        @Size(max = 255) String location,
        @DecimalMin("0.00") BigDecimal capacity,
        @Size(max = 100) String managerName
) {
}
