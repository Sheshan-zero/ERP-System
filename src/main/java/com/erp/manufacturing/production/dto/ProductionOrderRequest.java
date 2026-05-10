package com.erp.manufacturing.production.dto;

import com.erp.manufacturing.common.enums.ProductionOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductionOrderRequest(
        @NotNull Long finishedProductId,
        Long employeeId,
        LocalDateTime productionDate,
        @NotNull @Positive BigDecimal quantityToProduce,
        BigDecimal quantityProduced,
        ProductionOrderStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String priority,
        @Valid List<ProductionMaterialUsageRequest> materialUsages,
        @Valid List<ProductionAssignmentRequest> assignments
) {
}
