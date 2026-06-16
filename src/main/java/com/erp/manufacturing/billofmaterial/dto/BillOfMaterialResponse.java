package com.erp.manufacturing.billofmaterial.dto;

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
public class BillOfMaterialResponse {

    private Long bomId;

    private Long finishedProductId;

    private String finishedProductName;

    private Long rawMaterialId;

    private String rawMaterialName;

    private BigDecimal requiredQuantity;

    private BigDecimal wastagePercentage;
}