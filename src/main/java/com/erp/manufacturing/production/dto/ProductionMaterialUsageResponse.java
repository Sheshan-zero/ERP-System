package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterialUsageResponse {
    private Long usageId;
    private Long rawMaterialId;
    private BigDecimal quantityUsed;
    private LocalDateTime usageDate;
}
