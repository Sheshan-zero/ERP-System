package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.EntityLookupService;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.common.constants.DatabaseTableNames;
import com.erp.manufacturing.common.enums.AuditActionType;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.purchaseorder.dto.PurchaseBillDto;
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
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemStockService itemStockService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityLookupService entityLookupService;

    @Transactional(readOnly = true)
    public Page<PurchaseOrder> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
    }

    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderId() != null && purchaseOrderRepository.existsById(purchaseOrder.getPurchaseOrderId())) {
            throw new BusinessException("Purchase order already exists with id: " + purchaseOrder.getPurchaseOrderId());
        }

        if (purchaseOrder.getStatus() == null) {
            purchaseOrder.setStatus(PurchaseOrderStatus.Pending);
        }

        attachChildrenAndCalculateTotals(purchaseOrder);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder) {
        PurchaseOrder existingPurchaseOrder = getPurchaseOrderById(id);
        validateModifiable(existingPurchaseOrder);

        existingPurchaseOrder.setSupplierId(purchaseOrder.getSupplierId());
        existingPurchaseOrder.setEmployeeId(purchaseOrder.getEmployeeId());
        existingPurchaseOrder.setOrderDate(purchaseOrder.getOrderDate());
        existingPurchaseOrder.setExpectedDate(purchaseOrder.getExpectedDate());
        existingPurchaseOrder.setStatus(purchaseOrder.getStatus());

        mergePurchaseOrderItems(existingPurchaseOrder, purchaseOrder.getPurchaseOrderItems());
        existingPurchaseOrder.setTotalAmount(calculateTotalAmount(existingPurchaseOrder.getPurchaseOrderItems()));

        return purchaseOrderRepository.save(existingPurchaseOrder);
    }

    public void deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        validateModifiable(purchaseOrder);
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Transactional(readOnly = true)
    public PurchaseBillDto generateBill(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);
        BigDecimal totalAmount = purchaseOrder.getTotalAmount() == null ? BigDecimal.ZERO : purchaseOrder.getTotalAmount();

        return new PurchaseBillDto(
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getOrderDate(),
                totalAmount,
                purchaseOrder.getStatus() == null ? "Pending" : purchaseOrder.getStatus().name()
        );
    }

    public PurchaseOrder receivePurchaseOrder(Long id, Long employeeId, Long warehouseId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received) {
            throw new BusinessException("Purchase order is already received");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled purchase orders cannot be received");
        }
        if (purchaseOrder.getStatus() != PurchaseOrderStatus.Approved) {
            throw new BusinessException("Purchase order must be approved before receiving");
        }

        Employee employee = entityLookupService.getEmployeeReference(employeeId);
        Warehouse warehouse = entityLookupService.getRequiredWarehouseReference(
                warehouseId,
                "Warehouse ID is required to receive purchase order stock"
        );
        LocalDateTime now = LocalDateTime.now();

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            Item rawMaterial = entityLookupService.getItem(
                    item.getRawMaterialId(),
                    "Raw material not found with id: "
            );
            BigDecimal quantity = entityLookupService.requirePositiveQuantity(
                    item.getQuantity(),
                    "Purchase order item quantity must be greater than 0"
            );

            Item updatedRawMaterial = itemStockService.increaseStock(rawMaterial.getItemId(), warehouseId, quantity);

            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(updatedRawMaterial)
                    .warehouse(warehouse)
                    .employee(employee)
                    .transactionType(InventoryTransactionType.StockIn.getValue())
                    .quantity(quantity)
                    .transactionDate(now)
                    .remarks("Purchase order " + id + " received")
                    .build());
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.Received);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.RECEIVE.name())
                .recordId(id)
                .actionDate(now)
                .description("Received purchase order " + id)
                .build());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder approvePurchaseOrder(Long id, Long employeeId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Approved) {
            throw new BusinessException("Purchase order is already approved");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received) {
            throw new BusinessException("Received purchase orders cannot be approved");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled purchase orders cannot be approved");
        }

        Employee employee = entityLookupService.getEmployeeReference(employeeId);
        LocalDateTime now = LocalDateTime.now();

        purchaseOrder.setStatus(PurchaseOrderStatus.Approved);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.APPROVE.name())
                .recordId(id)
                .actionDate(now)
                .description("Approved purchase order " + id)
                .build());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private void attachChildrenAndCalculateTotals(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderItems() == null) {
            purchaseOrder.setPurchaseOrderItems(new ArrayList<>());
        }

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            item.setPurchaseOrder(purchaseOrder);
            calculateLineTotal(item);
        }
        purchaseOrder.setTotalAmount(calculateTotalAmount(purchaseOrder.getPurchaseOrderItems()));
    }

    private void calculateLineTotal(PurchaseOrderItem item) {
        BigDecimal quantity = entityLookupService.requirePositiveQuantity(
                item.getQuantity(),
                "Purchase order item quantity must be greater than 0"
        );
        BigDecimal unitPrice = entityLookupService.requirePositiveQuantity(
                item.getUnitPrice(),
                "Purchase order item unit price must be greater than 0"
        );

        item.setLineTotal(quantity.multiply(unitPrice));
    }

    private BigDecimal calculateTotalAmount(List<PurchaseOrderItem> items) {
        return items.stream()
                .map(PurchaseOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateModifiable(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus() != null && 
            purchaseOrder.getStatus() != PurchaseOrderStatus.Pending && 
            purchaseOrder.getStatus() != PurchaseOrderStatus.PendingApproval) {
            throw new BusinessException("Cannot modify a purchase order that is already "
                    + purchaseOrder.getStatus());
        }
    }

    private void mergePurchaseOrderItems(PurchaseOrder purchaseOrder, List<PurchaseOrderItem> incomingItems) {
        List<PurchaseOrderItem> requestedItems = incomingItems == null ? List.of() : incomingItems;
        Map<Long, PurchaseOrderItem> existingById = purchaseOrder.getPurchaseOrderItems().stream()
                .filter(item -> item.getPurchaseOrderItemId() != null)
                .collect(Collectors.toMap(PurchaseOrderItem::getPurchaseOrderItemId, Function.identity()));

        purchaseOrder.getPurchaseOrderItems().removeIf(existing ->
                existing.getPurchaseOrderItemId() != null
                        && requestedItems.stream()
                        .map(PurchaseOrderItem::getPurchaseOrderItemId)
                        .filter(Objects::nonNull)
                        .noneMatch(existing.getPurchaseOrderItemId()::equals)
        );

        for (PurchaseOrderItem incoming : requestedItems) {
            PurchaseOrderItem target;
            if (incoming.getPurchaseOrderItemId() == null) {
                target = new PurchaseOrderItem();
                target.setPurchaseOrder(purchaseOrder);
                purchaseOrder.getPurchaseOrderItems().add(target);
            } else {
                target = existingById.get(incoming.getPurchaseOrderItemId());
                if (target == null) {
                    throw new BusinessException("Purchase order item does not belong to this order: "
                            + incoming.getPurchaseOrderItemId());
                }
            }

            target.setRawMaterialId(incoming.getRawMaterialId());
            target.setQuantity(incoming.getQuantity());
            target.setUnitPrice(incoming.getUnitPrice());
            calculateLineTotal(target);
        }
    }
}
