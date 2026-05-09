package com.erp.manufacturing.production;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionOrderService {

    private static final String COMPLETED_STATUS = "Completed";
    private static final String STOCK_OUT_TRANSACTION_TYPE = "Stock Out";
    private static final String STOCK_IN_TRANSACTION_TYPE = "Stock In";

    private final ProductionOrderRepository productionOrderRepository;
    private final ItemRepository itemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityManager entityManager;

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

    public ProductionOrder completeProductionOrder(Long productionOrderId) {
        ProductionOrder productionOrder = getProductionOrderById(productionOrderId);

        if (COMPLETED_STATUS.equalsIgnoreCase(productionOrder.getStatus())) {
            throw new IllegalArgumentException("Production order is already completed");
        }
        if (productionOrder.getQuantityToProduce() == null
                || productionOrder.getQuantityToProduce().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity to produce must be greater than 0");
        }
        if (productionOrder.getFinishedProductId() == null) {
            throw new IllegalArgumentException("Finished product is required");
        }

        Employee employee = getEmployeeReference(productionOrder.getEmployeeId());
        LocalDateTime now = LocalDateTime.now();

        for (ProductionMaterialUsage usage : productionOrder.getMaterialUsages()) {
            Item rawMaterial = getItem(usage.getRawMaterialId(), "Raw material not found with id: ");
            BigDecimal quantityUsed = requirePositiveQuantity(usage.getQuantityUsed(), "Quantity used must be greater than 0");

            rawMaterial.setCurrentStock(getCurrentStock(rawMaterial).subtract(quantityUsed));
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(rawMaterial)
                    .employee(employee)
                    .transactionType(STOCK_OUT_TRANSACTION_TYPE)
                    .quantity(quantityUsed)
                    .transactionDate(now)
                    .remarks("Production order " + productionOrderId + " raw material usage")
                    .build());
        }

        Item finishedProduct = getItem(
                productionOrder.getFinishedProductId(),
                "Finished product not found with id: "
        );
        BigDecimal producedQuantity = productionOrder.getQuantityToProduce();

        finishedProduct.setCurrentStock(getCurrentStock(finishedProduct).add(producedQuantity));
        inventoryTransactionRepository.save(InventoryTransaction.builder()
                .item(finishedProduct)
                .employee(employee)
                .transactionType(STOCK_IN_TRANSACTION_TYPE)
                .quantity(producedQuantity)
                .transactionDate(now)
                .remarks("Production order " + productionOrderId + " completed")
                .build());

        productionOrder.setStatus(COMPLETED_STATUS);
        productionOrder.setQuantityProduced(producedQuantity);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName("PRODUCTIONORDER")
                .actionType("COMPLETE")
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

    private Item getItem(Long itemId, String messagePrefix) {
        if (itemId == null) {
            throw new IllegalArgumentException(messagePrefix + "null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(messagePrefix + itemId));
    }

    private Employee getEmployeeReference(Long employeeId) {
        if (employeeId == null) {
            return null;
        }

        return entityManager.getReference(Employee.class, employeeId);
    }

    private BigDecimal getCurrentStock(Item item) {
        return item.getCurrentStock() == null ? BigDecimal.ZERO : item.getCurrentStock();
    }

    private BigDecimal requirePositiveQuantity(BigDecimal quantity, String message) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }

        return quantity;
    }
}
