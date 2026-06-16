package com.erp.manufacturing.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {

        private Long warehouseId;

        private String warehouseName;

        private String location;

        private BigDecimal capacity;

        private String managerName;
}