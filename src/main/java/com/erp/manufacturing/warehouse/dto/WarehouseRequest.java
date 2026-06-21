package com.erp.manufacturing.warehouse.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {
    private @NotBlank @Size(max = 100) String warehouseName;
    private @Size(max = 255) String location;
    private @DecimalMin("0.00") BigDecimal capacity;
    private @Size(max = 100) String managerName;
}
