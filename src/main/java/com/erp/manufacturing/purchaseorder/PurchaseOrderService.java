package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.constants.DatabaseTableNames;
import com.erp.manufacturing.common.enums.AuditActionType;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.enums.PurchaseOrderStatus;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.purchaseorder.dto.PurchaseBillDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemRepository itemRepository;
    private final ItemStockService itemStockService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityManager entityManager;

    @Value("${app.purchase.approval-threshold:100000}")
    private BigDecimal approvalThreshold;

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

        attachItemsAndCalculateTotals(purchaseOrder);
        applyApprovalStatus(purchaseOrder);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder) {
        PurchaseOrder existingPurchaseOrder = getPurchaseOrderById(id);

        existingPurchaseOrder.setSupplierId(purchaseOrder.getSupplierId());
        existingPurchaseOrder.setEmployeeId(purchaseOrder.getEmployeeId());
        existingPurchaseOrder.setOrderDate(purchaseOrder.getOrderDate());
        existingPurchaseOrder.setExpectedDate(purchaseOrder.getExpectedDate());
        existingPurchaseOrder.setStatus(purchaseOrder.getStatus());

        existingPurchaseOrder.getPurchaseOrderItems().clear();
        if (purchaseOrder.getPurchaseOrderItems() != null) {
            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                item.setPurchaseOrder(existingPurchaseOrder);
                calculateLineTotal(item);
                existingPurchaseOrder.getPurchaseOrderItems().add(item);
            }
        }
        existingPurchaseOrder.setTotalAmount(calculateTotalAmount(existingPurchaseOrder.getPurchaseOrderItems()));
        applyApprovalStatus(existingPurchaseOrder);

        return purchaseOrderRepository.save(existingPurchaseOrder);
    }

    public void deletePurchaseOrder(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purchase order not found with id: " + id);
        }

        purchaseOrderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PurchaseBillDto generateBill(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);

        return new PurchaseBillDto(
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getOrderDate(),
                purchaseOrder.getTotalAmount() == null ? BigDecimal.ZERO : purchaseOrder.getTotalAmount(),
                purchaseOrder.getStatus() == null ? null : purchaseOrder.getStatus().name()
        );
    }

    public PurchaseOrder approvePurchaseOrder(Long purchaseOrderId, Long employeeId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.PendingApproval) {
            throw new BusinessException("Only purchase orders pending approval can be approved");
        }

        Employee employee = getEmployeeReference(employeeId);
        purchaseOrder.setStatus(PurchaseOrderStatus.Approved);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.APPROVE.name())
                .recordId(purchaseOrderId)
                .actionDate(LocalDateTime.now())
                .description("Approved purchase order " + purchaseOrderId)
                .build());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder receivePurchaseOrder(Long purchaseOrderId, Long employeeId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received) {
            throw new BusinessException("Purchase order is already received");
        }
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled purchase orders cannot be received");
        }

        LocalDateTime now = LocalDateTime.now();
        Long auditEmployeeId = employeeId != null ? employeeId : purchaseOrder.getEmployeeId();
        Employee employee = getEmployeeReference(auditEmployeeId);

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getPurchaseOrderItems()) {
            Item rawMaterial = getItem(
                    purchaseOrderItem.getRawMaterialId(),
                    "Raw material not found with id: "
            );
            BigDecimal quantity = requirePositiveQuantity(
                    purchaseOrderItem.getQuantity(),
                    "Purchase order item quantity must be greater than 0"
            );

            Item updatedRawMaterial = itemStockService.increaseStock(rawMaterial.getItemId(), quantity);
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(updatedRawMaterial)
                    .employee(employee)
                    .transactionType(InventoryTransactionType.StockIn.getValue())
                    .quantity(quantity)
                    .transactionDate(now)
                    .remarks("Purchase order " + purchaseOrderId + " received")
                    .build());
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.Received);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.PURCHASE_ORDER)
                .actionType(AuditActionType.RECEIVE.name())
                .recordId(purchaseOrderId)
                .actionDate(now)
                .description("Received purchase order " + purchaseOrderId)
                .build());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private void attachItemsAndCalculateTotals(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderItems() == null) {
            purchaseOrder.setPurchaseOrderItems(new ArrayList<>());
            purchaseOrder.setTotalAmount(BigDecimal.ZERO);
            return;
        }

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            item.setPurchaseOrder(purchaseOrder);
            calculateLineTotal(item);
        }
        purchaseOrder.setTotalAmount(calculateTotalAmount(purchaseOrder.getPurchaseOrderItems()));
    }

    private Item getItem(Long itemId, String messagePrefix) {
        if (itemId == null) {
            throw new BusinessException(messagePrefix + "null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(messagePrefix + itemId));
    }

    private BigDecimal getCurrentStock(Item item) {
        return item.getCurrentStock() == null ? BigDecimal.ZERO : item.getCurrentStock();
    }

    private BigDecimal requirePositiveQuantity(BigDecimal quantity, String message) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(message);
        }

        return quantity;
    }

    private void calculateLineTotal(PurchaseOrderItem item) {
        BigDecimal quantity = requirePositiveQuantity(
                item.getQuantity(),
                "Purchase order item quantity must be greater than 0"
        );
        BigDecimal unitPrice = requirePositiveQuantity(
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

    private Employee getEmployeeReference(Long employeeId) {
        if (employeeId == null) {
            return null;
        }

        return entityManager.getReference(Employee.class, employeeId);
    }

    private void applyApprovalStatus(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.Received
                || purchaseOrder.getStatus() == PurchaseOrderStatus.Cancelled
                || purchaseOrder.getStatus() == PurchaseOrderStatus.Approved) {
            return;
        }

        BigDecimal totalAmount = purchaseOrder.getTotalAmount() == null ? BigDecimal.ZERO : purchaseOrder.getTotalAmount();
        purchaseOrder.setStatus(totalAmount.compareTo(approvalThreshold) > 0
                ? PurchaseOrderStatus.PendingApproval
                : PurchaseOrderStatus.Pending);
    }
}
