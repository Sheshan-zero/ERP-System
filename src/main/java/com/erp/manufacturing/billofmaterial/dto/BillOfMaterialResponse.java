package com.erp.manufacturing.billofmaterial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterialResponse {
    private Long bomId;
    private Long finishedProductId;
    private String finishedProductName;
    private Long rawMaterialId;
    private String rawMaterialName;
    private BigDecimal requiredQuantity;
    private BigDecimal wastagePercentage;
}
