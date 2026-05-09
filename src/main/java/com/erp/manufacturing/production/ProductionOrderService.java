package com.erp.manufacturing.production;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;

    @Transactional(readOnly = true)
    public List<ProductionOrder> getAllProductionOrders() {
        return productionOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductionOrder getProductionOrderById(Long id) {
        return productionOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Production order not found with id: " + id));
    }

    public ProductionOrder createProductionOrder(ProductionOrder productionOrder) {
        if (productionOrder.getProductionOrderId() != null
                && productionOrderRepository.existsById(productionOrder.getProductionOrderId())) {
            throw new IllegalArgumentException("Production order already exists with id: "
                    + productionOrder.getProductionOrderId());
        }

        attachChildren(productionOrder);
        return productionOrderRepository.save(productionOrder);
    }

    public ProductionOrder updateProductionOrder(Long id, ProductionOrder productionOrder) {
        ProductionOrder existingProductionOrder = getProductionOrderById(id);

        existingProductionOrder.setFinishedProductId(productionOrder.getFinishedProductId());
        existingProductionOrder.setEmployeeId(productionOrder.getEmployeeId());
        existingProductionOrder.setProductionDate(productionOrder.getProductionDate());
        existingProductionOrder.setQuantityToProduce(productionOrder.getQuantityToProduce());
        existingProductionOrder.setQuantityProduced(productionOrder.getQuantityProduced());
        existingProductionOrder.setStatus(productionOrder.getStatus());
        existingProductionOrder.setStartDate(productionOrder.getStartDate());
        existingProductionOrder.setEndDate(productionOrder.getEndDate());
        existingProductionOrder.setPriority(productionOrder.getPriority());

        existingProductionOrder.getMaterialUsages().clear();
        if (productionOrder.getMaterialUsages() != null) {
            for (ProductionMaterialUsage usage : productionOrder.getMaterialUsages()) {
                usage.setProductionOrder(existingProductionOrder);
                existingProductionOrder.getMaterialUsages().add(usage);
            }
        }

        existingProductionOrder.getAssignments().clear();
        if (productionOrder.getAssignments() != null) {
            for (ProductionAssignment assignment : productionOrder.getAssignments()) {
                assignment.setProductionOrder(existingProductionOrder);
                existingProductionOrder.getAssignments().add(assignment);
            }
        }

        return productionOrderRepository.save(existingProductionOrder);
    }

    public void deleteProductionOrder(Long id) {
        if (!productionOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Production order not found with id: " + id);
        }

        productionOrderRepository.deleteById(id);
    }

    private void attachChildren(ProductionOrder productionOrder) {
        if (productionOrder.getMaterialUsages() == null) {
            productionOrder.setMaterialUsages(new ArrayList<>());
        }
        if (productionOrder.getAssignments() == null) {
            productionOrder.setAssignments(new ArrayList<>());
        }

        for (ProductionMaterialUsage usage : productionOrder.getMaterialUsages()) {
            usage.setProductionOrder(productionOrder);
        }
        for (ProductionAssignment assignment : productionOrder.getAssignments()) {
            assignment.setProductionOrder(productionOrder);
        }
    }
}
