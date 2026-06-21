package com.erp.manufacturing.production;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.billofmaterial.BillOfMaterial;
import com.erp.manufacturing.billofmaterial.BillOfMaterialRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.EntityLookupService;
import com.erp.manufacturing.common.constants.DatabaseTableNames;
import com.erp.manufacturing.common.enums.AuditActionType;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.enums.ProductionOrderStatus;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.warehouse.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ItemStockService itemStockService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final EntityLookupService entityLookupService;

    @Transactional(readOnly = true)
    public Page<ProductionOrder> getAllProductionOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProductionOrder getProductionOrderById(Long id) {
        return productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Production order not found with id: " + id));
    }

    public ProductionOrder createProductionOrder(ProductionOrder productionOrder) {
        if (productionOrder.getProductionOrderId() != null
                && productionOrderRepository.existsById(productionOrder.getProductionOrderId())) {
            throw new BusinessException("Production order already exists with id: "
                    + productionOrder.getProductionOrderId());
        }

        attachChildren(productionOrder);
        return productionOrderRepository.save(productionOrder);
    }

    public ProductionOrder updateProductionOrder(Long id, ProductionOrder productionOrder) {
        ProductionOrder existingProductionOrder = getProductionOrderById(id);
        validateModifiable(existingProductionOrder);

        existingProductionOrder.setFinishedProductId(productionOrder.getFinishedProductId());
        existingProductionOrder.setEmployeeId(productionOrder.getEmployeeId());
        existingProductionOrder.setProductionDate(productionOrder.getProductionDate());
        existingProductionOrder.setQuantityToProduce(productionOrder.getQuantityToProduce());
        existingProductionOrder.setQuantityProduced(productionOrder.getQuantityProduced());
        existingProductionOrder.setStatus(productionOrder.getStatus());
        existingProductionOrder.setStartDate(productionOrder.getStartDate());
        existingProductionOrder.setEndDate(productionOrder.getEndDate());
        existingProductionOrder.setPriority(productionOrder.getPriority());

        mergeMaterialUsages(existingProductionOrder, productionOrder.getMaterialUsages());
        mergeAssignments(existingProductionOrder, productionOrder.getAssignments());

        return productionOrderRepository.save(existingProductionOrder);
    }

    public void deleteProductionOrder(Long id) {
        ProductionOrder productionOrder = getProductionOrderById(id);
        validateModifiable(productionOrder);
        productionOrderRepository.delete(productionOrder);
    }

    public ProductionOrder completeProductionOrder(Long productionOrderId, Long warehouseId) {
        ProductionOrder productionOrder = getProductionOrderById(productionOrderId);

        if (productionOrder.getStatus() == ProductionOrderStatus.Completed) {
            throw new BusinessException("Production order is already completed");
        }
        if (productionOrder.getStatus() == ProductionOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled production orders cannot be completed");
        }
        if (productionOrder.getQuantityToProduce() == null
                || productionOrder.getQuantityToProduce().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Quantity to produce must be greater than 0");
        }
        if (productionOrder.getFinishedProductId() == null) {
            throw new BusinessException("Finished product is required");
        }

        Employee employee = entityLookupService.getEmployeeReference(productionOrder.getEmployeeId());
        Warehouse warehouse = entityLookupService.getRequiredWarehouseReference(
                warehouseId,
                "Warehouse ID is required to complete production stock movements"
        );
        LocalDateTime now = LocalDateTime.now();
        explodeBomIfNoMaterialUsage(productionOrder);

        for (ProductionMaterialUsage usage : productionOrder.getMaterialUsages()) {
            Item rawMaterial = entityLookupService.getItem(usage.getRawMaterialId(), "Raw material not found with id: ");
            BigDecimal quantityUsed = entityLookupService.requirePositiveQuantity(
                    usage.getQuantityUsed(),
                    "Quantity used must be greater than 0"
            );
            Item updatedRawMaterial = itemStockService.decreaseStock(rawMaterial.getItemId(), warehouseId, quantityUsed);
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(updatedRawMaterial)
                    .warehouse(warehouse)
                    .employee(employee)
                    .transactionType(InventoryTransactionType.StockOut.getValue())
                    .quantity(quantityUsed)
                    .transactionDate(now)
                    .remarks("Production order " + productionOrderId + " raw material usage")
                    .build());
        }

        Item finishedProduct = entityLookupService.getItem(
                productionOrder.getFinishedProductId(),
                "Finished product not found with id: "
        );
        BigDecimal producedQuantity = productionOrder.getQuantityToProduce();

        Item updatedFinishedProduct = itemStockService.increaseStock(finishedProduct.getItemId(), warehouseId, producedQuantity);
        inventoryTransactionRepository.save(InventoryTransaction.builder()
                .item(updatedFinishedProduct)
                .warehouse(warehouse)
                .employee(employee)
                .transactionType(InventoryTransactionType.StockIn.getValue())
                .quantity(producedQuantity)
                .transactionDate(now)
                .remarks("Production order " + productionOrderId + " completed")
                .build());

        productionOrder.setStatus(ProductionOrderStatus.Completed);
        productionOrder.setQuantityProduced(producedQuantity);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PRODUCTION_ORDER)
                .actionType(AuditActionType.COMPLETE.name())
                .recordId(productionOrderId)
                .actionDate(now)
                .description("Completed production order " + productionOrderId)
                .build());

        return productionOrderRepository.save(productionOrder);
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

    private void explodeBomIfNoMaterialUsage(ProductionOrder productionOrder) {
        if (productionOrder.getMaterialUsages() != null && !productionOrder.getMaterialUsages().isEmpty()) {
            return;
        }

        List<BillOfMaterial> billOfMaterials = billOfMaterialRepository.findByFinishedProductItemId(
                productionOrder.getFinishedProductId()
        );
        if (billOfMaterials.isEmpty()) {
            throw new BusinessException("No BOM found for finished product id: " + productionOrder.getFinishedProductId());
        }

        List<ProductionMaterialUsage> generatedUsages = new ArrayList<>();
        for (BillOfMaterial billOfMaterial : billOfMaterials) {
            BigDecimal wastageMultiplier = BigDecimal.ONE;
            if (billOfMaterial.getWastagePercentage() != null) {
                wastageMultiplier = wastageMultiplier.add(
                        billOfMaterial.getWastagePercentage().divide(BigDecimal.valueOf(100))
                );
            }

            ProductionMaterialUsage usage = ProductionMaterialUsage.builder()
                    .productionOrder(productionOrder)
                    .rawMaterialId(billOfMaterial.getRawMaterial().getItemId())
                    .quantityUsed(billOfMaterial.getRequiredQuantity()
                            .multiply(productionOrder.getQuantityToProduce())
                            .multiply(wastageMultiplier))
                    .usageDate(LocalDateTime.now())
                    .build();
            generatedUsages.add(usage);
        }

        productionOrder.setMaterialUsages(generatedUsages);
    }

    private void validateModifiable(ProductionOrder productionOrder) {
        if (productionOrder.getStatus() != null && productionOrder.getStatus() != ProductionOrderStatus.Planned) {
            throw new BusinessException("Cannot modify a production order that is already "
                    + productionOrder.getStatus());
        }
    }

    private void mergeMaterialUsages(ProductionOrder productionOrder, List<ProductionMaterialUsage> incomingUsages) {
        List<ProductionMaterialUsage> requestedUsages = incomingUsages == null ? List.of() : incomingUsages;
        Map<Long, ProductionMaterialUsage> existingById = productionOrder.getMaterialUsages().stream()
                .filter(usage -> usage.getUsageId() != null)
                .collect(Collectors.toMap(ProductionMaterialUsage::getUsageId, Function.identity()));

        productionOrder.getMaterialUsages().removeIf(existing ->
                existing.getUsageId() != null
                        && requestedUsages.stream()
                        .map(ProductionMaterialUsage::getUsageId)
                        .filter(Objects::nonNull)
                        .noneMatch(existing.getUsageId()::equals)
        );

        for (ProductionMaterialUsage incoming : requestedUsages) {
            ProductionMaterialUsage target;
            if (incoming.getUsageId() == null) {
                target = new ProductionMaterialUsage();
                target.setProductionOrder(productionOrder);
                productionOrder.getMaterialUsages().add(target);
            } else {
                target = existingById.get(incoming.getUsageId());
                if (target == null) {
                    throw new BusinessException("Material usage does not belong to this production order: "
                            + incoming.getUsageId());
                }
            }

            target.setRawMaterialId(incoming.getRawMaterialId());
            target.setQuantityUsed(incoming.getQuantityUsed());
            target.setUsageDate(incoming.getUsageDate());
        }
    }

    private void mergeAssignments(ProductionOrder productionOrder, List<ProductionAssignment> incomingAssignments) {
        List<ProductionAssignment> requestedAssignments = incomingAssignments == null ? List.of() : incomingAssignments;
        Map<Long, ProductionAssignment> existingById = productionOrder.getAssignments().stream()
                .filter(assignment -> assignment.getAssignmentId() != null)
                .collect(Collectors.toMap(ProductionAssignment::getAssignmentId, Function.identity()));

        productionOrder.getAssignments().removeIf(existing ->
                existing.getAssignmentId() != null
                        && requestedAssignments.stream()
                        .map(ProductionAssignment::getAssignmentId)
                        .filter(Objects::nonNull)
                        .noneMatch(existing.getAssignmentId()::equals)
        );

        for (ProductionAssignment incoming : requestedAssignments) {
            ProductionAssignment target;
            if (incoming.getAssignmentId() == null) {
                target = new ProductionAssignment();
                target.setProductionOrder(productionOrder);
                productionOrder.getAssignments().add(target);
            } else {
                target = existingById.get(incoming.getAssignmentId());
                if (target == null) {
                    throw new BusinessException("Assignment does not belong to this production order: "
                            + incoming.getAssignmentId());
                }
            }

            target.setEmployeeId(incoming.getEmployeeId());
            target.setRole(incoming.getRole());
            target.setHoursWorked(incoming.getHoursWorked());
            target.setAssignmentDate(incoming.getAssignmentDate());
        }
    }
}
