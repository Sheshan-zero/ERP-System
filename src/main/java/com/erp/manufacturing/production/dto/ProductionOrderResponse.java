package com.erp.manufacturing.production.dto;

import com.erp.manufacturing.common.enums.ProductionOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductionOrderResponse(
        Long productionOrderId,
        Long finishedProductId,
        Long employeeId,
        LocalDateTime productionDate,
        BigDecimal quantityToProduce,
        BigDecimal quantityProduced,
        ProductionOrderStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String priority,
        List<ProductionMaterialUsageResponse> materialUsages,
        List<ProductionAssignmentResponse> assignments
) {
}
