package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.common.enums.ProductionOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderRequest {
    private @NotNull Long finishedProductId;
    private Long employeeId;
    private LocalDateTime productionDate;
    private @NotNull @Positive BigDecimal quantityToProduce;
    private BigDecimal quantityProduced;
    private ProductionOrderStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String priority;
    private @Valid List<ProductionMaterialUsageRequest> materialUsages;
    private @Valid List<ProductionAssignmentRequest> assignments;
}
