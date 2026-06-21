package com.erp.manufacturing.production;

import com.erp.manufacturing.production.dto.ProductionAssignmentResponse;
import com.erp.manufacturing.production.dto.ProductionMaterialUsageResponse;
import com.erp.manufacturing.production.dto.ProductionOrderRequest;
import com.erp.manufacturing.production.dto.ProductionOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductionOrderMapper {

    public ProductionOrder toEntity(ProductionOrderRequest request) {
        ProductionOrder productionOrder = ProductionOrder.builder()
                .finishedProductId(request.getFinishedProductId())
                .employeeId(request.getEmployeeId())
                .productionDate(request.getProductionDate())
                .quantityToProduce(request.getQuantityToProduce())
                .quantityProduced(request.getQuantityProduced())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority())
                .materialUsages(new ArrayList<>())
                .assignments(new ArrayList<>())
                .build();

        if (request.getMaterialUsages() != null) {
            request.getMaterialUsages().forEach(usage -> productionOrder.getMaterialUsages().add(
                    ProductionMaterialUsage.builder()
                            .usageId(usage.getUsageId())
                            .productionOrder(productionOrder)
                            .rawMaterialId(usage.getRawMaterialId())
                            .quantityUsed(usage.getQuantityUsed())
                            .usageDate(usage.getUsageDate())
                            .build()
            ));
        }
        if (request.getAssignments() != null) {
            request.getAssignments().forEach(assignment -> productionOrder.getAssignments().add(
                    ProductionAssignment.builder()
                            .assignmentId(assignment.getAssignmentId())
                            .productionOrder(productionOrder)
                            .employeeId(assignment.getEmployeeId())
                            .role(assignment.getRole())
                            .hoursWorked(assignment.getHoursWorked())
                            .assignmentDate(assignment.getAssignmentDate())
                            .build()
            ));
        }

        return productionOrder;
    }

    public ProductionOrderResponse toResponse(ProductionOrder productionOrder) {
        List<ProductionMaterialUsageResponse> usages = productionOrder.getMaterialUsages().stream()
                .map(usage -> new ProductionMaterialUsageResponse(
                        usage.getUsageId(),
                        usage.getRawMaterialId(),
                        usage.getQuantityUsed(),
                        usage.getUsageDate()
                ))
                .toList();
        List<ProductionAssignmentResponse> assignments = productionOrder.getAssignments().stream()
                .map(assignment -> new ProductionAssignmentResponse(
                        assignment.getAssignmentId(),
                        assignment.getEmployeeId(),
                        assignment.getRole(),
                        assignment.getHoursWorked(),
                        assignment.getAssignmentDate()
                ))
                .toList();

        return new ProductionOrderResponse(
                productionOrder.getProductionOrderId(),
                productionOrder.getFinishedProductId(),
                productionOrder.getEmployeeId(),
                productionOrder.getProductionDate(),
                productionOrder.getQuantityToProduce(),
                productionOrder.getQuantityProduced(),
                productionOrder.getStatus(),
                productionOrder.getStartDate(),
                productionOrder.getEndDate(),
                productionOrder.getPriority(),
                usages,
                assignments
        );
    }

    public Page<ProductionOrderResponse> toResponsePage(Page<ProductionOrder> page) {
        return page.map(this::toResponse);
    }
}
