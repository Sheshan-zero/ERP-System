package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.ProductionOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderResponse {
    private Long productionOrderId;
    private Long finishedProductId;
    private Long employeeId;
    private LocalDateTime productionDate;
    private BigDecimal quantityToProduce;
    private BigDecimal quantityProduced;
    private ProductionOrderStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String priority;
    private List<ProductionMaterialUsageResponse> materialUsages;
    private List<ProductionAssignmentResponse> assignments;
}
