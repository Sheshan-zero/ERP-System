package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.accounting.GeneralLedgerService;
import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.EntityLookupService;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.common.constants.DatabaseTableNames;
import com.erp.manufacturing.common.enums.AuditActionType;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import com.erp.manufacturing.config.SystemConfigurationService;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.notification.NotificationService;
import com.erp.manufacturing.supplier.Supplier;
import com.erp.manufacturing.supplier.SupplierRepository;
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

    private static final BigDecimal DEFAULT_APPROVAL_THRESHOLD = new BigDecimal("100000");

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ItemStockService itemStockService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityLookupService entityLookupService;
    private final SystemConfigurationService systemConfigurationService;
    private final GeneralLedgerService generalLedgerService;
    private final NotificationService notificationService;

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
        if (purchaseOrder.getPurchaseOrderId() != null
                && purchaseOrderRepository.existsById(purchaseOrder.getPurchaseOrderId())) {
            throw new BusinessException("Purchase order already exists with id: "
                    + purchaseOrder.getPurchaseOrderId());
        }

        ensureSupplierExists(purchaseOrder.getSupplierId());
        attachChildrenAndCalculateTotals(purchaseOrder);
        if (purchaseOrder.getOrderDate() == null) {
            purchaseOrder.setOrderDate(LocalDateTime.now());
        }
        if (purchaseOrder.getStatus() == null) {
            purchaseOrder.setStatus(resolveInitialStatus(purchaseOrder.getTotalAmount()));
        }

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        if (savedPurchaseOrder.getStatus() == PurchaseOrderStatus.PendingApproval) {
            notificationService.queueSystemNotification(
                    null,
                    "Purchase order requires approval",
                    "Purchase order " + savedPurchaseOrder.getPurchaseOrderId() + " requires manager approval.",
                    DatabaseTableNames.PURCHASE_ORDER,
                    savedPurchaseOrder.getPurchaseOrderId()
            );
        }
        return savedPurchaseOrder;
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder) {
        PurchaseOrder existingPurchaseOrder = getPurchaseOrderById(id);
        validateModifiable(existingPurchaseOrder);
        ensureSupplierExists(purchaseOrder.getSupplierId());

        existingPurchaseOrder.setSupplierId(purchaseOrder.getSupplierId());
        existingPurchaseOrder.setEmployeeId(purchaseOrder.getEmployeeId());
        existingPurchaseOrder.setOrderDate(purchaseOrder.getOrderDate());
        existingPurchaseOrder.setExpectedDate(purchaseOrder.getExpectedDate());
        existingPurchaseOrder.setStatus(purchaseOrder.getStatus());

        mergePurchaseOrderItems(existingPurchaseOrder, purchaseOrder.getPurchaseOrderItems());
        existingPurchaseOrder.setTotalAmount(calculateTotalAmount(existingPurchaseOrder.getPurchaseOrderItems()));
        if (existingPurchaseOrder.getStatus() == null) {
            existingPurchaseOrder.setStatus(resolveInitialStatus(existingPurchaseOrder.getTotalAmount()));
        }

        return purchaseOrderRepository.save(existingPurchaseOrder);
    }

    public void deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        validateModifiable(purchaseOrder);
        purchaseOrderRepository.delete(purchaseOrder);
    }

    public PurchaseOrder approvePurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Approved) {
            throw new BusinessException("Purchase order is already approved");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received) {
            throw new BusinessException("Received purchase orders cannot be approved");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled purchase orders cannot be approved");
        }

        LocalDateTime now = LocalDateTime.now();
        Employee employee = entityLookupService.getEmployeeReference(purchaseOrder.getEmployeeId());
        purchaseOrder.setStatus(PurchaseOrderStatus.Approved);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.APPROVE.name())
                .recordId(purchaseOrderId)
                .actionDate(now)
                .description("Approved purchase order " + purchaseOrderId)
                .build());

        notificationService.queueSystemNotification(
                null,
                "Purchase order approved",
                "Purchase order " + purchaseOrderId + " was approved.",
                DatabaseTableNames.PURCHASE_ORDER,
                purchaseOrderId
        );

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder receivePurchaseOrder(Long purchaseOrderId, Long warehouseId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received) {
            throw new BusinessException("Purchase order is already received");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled purchase orders cannot be received");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.PendingApproval) {
            throw new BusinessException("Purchase order must be approved before receiving");
        }

        Employee employee = entityLookupService.getEmployeeReference(purchaseOrder.getEmployeeId());
        Warehouse warehouse = entityLookupService.getRequiredWarehouseReference(
                warehouseId,
                "Warehouse ID is required to receive purchase order stock"
        );
        LocalDateTime now = LocalDateTime.now();

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getPurchaseOrderItems()) {
            Item rawMaterial = entityLookupService.getItem(
                    purchaseOrderItem.getRawMaterialId(),
                    "Raw material not found with id: "
            );
            BigDecimal quantity = entityLookupService.requirePositiveQuantity(
                    purchaseOrderItem.getQuantity(),
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
                    .remarks("Purchase order " + purchaseOrderId + " received")
                    .build());
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.Received);
        BigDecimal totalAmount = purchaseOrder.getTotalAmount() == null
                ? calculateTotalAmount(purchaseOrder.getPurchaseOrderItems())
                : purchaseOrder.getTotalAmount();

        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            generalLedgerService.recordBalancedEntry(
                    GeneralLedgerService.INVENTORY,
                    "Inventory",
                    GeneralLedgerService.ACCOUNTS_PAYABLE,
                    "Accounts Payable",
                    totalAmount,
                    DatabaseTableNames.PURCHASE_ORDER,
                    purchaseOrderId,
                    "Received purchase order " + purchaseOrderId
            );
        }

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.RECEIVE.name())
                .recordId(purchaseOrderId)
                .actionDate(now)
                .description("Received purchase order " + purchaseOrderId)
                .build());

        notificationService.queueSystemNotification(
                getSupplierEmail(purchaseOrder.getSupplierId()),
                "Purchase order received",
                "Purchase order " + purchaseOrderId + " was received into warehouse " + warehouseId + ".",
                DatabaseTableNames.PURCHASE_ORDER,
                purchaseOrderId
        );

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
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PurchaseOrderStatus resolveInitialStatus(BigDecimal totalAmount) {
        BigDecimal approvalThreshold = systemConfigurationService.getBigDecimal(
                SystemConfigurationService.PURCHASE_APPROVAL_THRESHOLD,
                DEFAULT_APPROVAL_THRESHOLD
        );
        return totalAmount != null && totalAmount.compareTo(approvalThreshold) > 0
                ? PurchaseOrderStatus.PendingApproval
                : PurchaseOrderStatus.Pending;
    }

    private void validateModifiable(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus() != null
                && purchaseOrder.getStatus() != PurchaseOrderStatus.Pending
                && purchaseOrder.getStatus() != PurchaseOrderStatus.PendingApproval) {
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

    private void ensureSupplierExists(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException("Supplier ID is required");
        }
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
    }

    private String getSupplierEmail(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(Supplier::getEmail)
                .orElse(null);
    }
}
