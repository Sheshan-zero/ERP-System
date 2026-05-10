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
                .finishedProductId(request.finishedProductId())
                .employeeId(request.employeeId())
                .productionDate(request.productionDate())
                .quantityToProduce(request.quantityToProduce())
                .quantityProduced(request.quantityProduced())
                .status(request.status())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .priority(request.priority())
                .materialUsages(new ArrayList<>())
                .assignments(new ArrayList<>())
                .build();

        if (request.materialUsages() != null) {
            request.materialUsages().forEach(usage -> productionOrder.getMaterialUsages().add(
                    ProductionMaterialUsage.builder()
                            .usageId(usage.usageId())
                            .productionOrder(productionOrder)
                            .rawMaterialId(usage.rawMaterialId())
                            .quantityUsed(usage.quantityUsed())
                            .usageDate(usage.usageDate())
                            .build()
            ));
        }
        if (request.assignments() != null) {
            request.assignments().forEach(assignment -> productionOrder.getAssignments().add(
                    ProductionAssignment.builder()
                            .assignmentId(assignment.assignmentId())
                            .productionOrder(productionOrder)
                            .employeeId(assignment.employeeId())
                            .role(assignment.role())
                            .hoursWorked(assignment.hoursWorked())
                            .assignmentDate(assignment.assignmentDate())
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
