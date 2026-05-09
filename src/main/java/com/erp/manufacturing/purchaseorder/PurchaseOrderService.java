package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
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
public class PurchaseOrderService {

    private static final String RECEIVED_STATUS = "Received";
    private static final String STOCK_IN_TRANSACTION_TYPE = "Stock In";

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemRepository itemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
    }

    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderId() != null
                && purchaseOrderRepository.existsById(purchaseOrder.getPurchaseOrderId())) {
            throw new IllegalArgumentException("Purchase order already exists with id: "
                    + purchaseOrder.getPurchaseOrderId());
        }

        attachItems(purchaseOrder);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder) {
        PurchaseOrder existingPurchaseOrder = getPurchaseOrderById(id);

        existingPurchaseOrder.setSupplierId(purchaseOrder.getSupplierId());
        existingPurchaseOrder.setOrderDate(purchaseOrder.getOrderDate());
        existingPurchaseOrder.setExpectedDate(purchaseOrder.getExpectedDate());
        existingPurchaseOrder.setStatus(purchaseOrder.getStatus());
        existingPurchaseOrder.setTotalAmount(purchaseOrder.getTotalAmount());

        existingPurchaseOrder.getPurchaseOrderItems().clear();
        if (purchaseOrder.getPurchaseOrderItems() != null) {
            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                item.setPurchaseOrder(existingPurchaseOrder);
                existingPurchaseOrder.getPurchaseOrderItems().add(item);
            }
        }

        return purchaseOrderRepository.save(existingPurchaseOrder);
    }

    public void deletePurchaseOrder(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Purchase order not found with id: " + id);
        }

        purchaseOrderRepository.deleteById(id);
    }

    public PurchaseOrder receivePurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);

        if (RECEIVED_STATUS.equalsIgnoreCase(purchaseOrder.getStatus())) {
            throw new IllegalArgumentException("Purchase order is already received");
        }

        LocalDateTime now = LocalDateTime.now();

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getPurchaseOrderItems()) {
            Item rawMaterial = getItem(
                    purchaseOrderItem.getRawMaterialId(),
                    "Raw material not found with id: "
            );
            BigDecimal quantity = requirePositiveQuantity(
                    purchaseOrderItem.getQuantity(),
                    "Purchase order item quantity must be greater than 0"
            );

            rawMaterial.setCurrentStock(getCurrentStock(rawMaterial).add(quantity));
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(rawMaterial)
                    .transactionType(STOCK_IN_TRANSACTION_TYPE)
                    .quantity(quantity)
                    .transactionDate(now)
                    .remarks("Purchase order " + purchaseOrderId + " received")
                    .build());
        }

        purchaseOrder.setStatus(RECEIVED_STATUS);

        auditLogRepository.save(AuditLog.builder()
                .tableName("PURCHASEORDER")
                .actionType("RECEIVE")
                .recordId(purchaseOrderId)
                .actionDate(now)
                .description("Received purchase order " + purchaseOrderId)
                .build());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private void attachItems(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderItems() == null) {
            purchaseOrder.setPurchaseOrderItems(new ArrayList<>());
            return;
        }

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            item.setPurchaseOrder(purchaseOrder);
        }
    }

    private Item getItem(Long itemId, String messagePrefix) {
        if (itemId == null) {
            throw new IllegalArgumentException(messagePrefix + "null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(messagePrefix + itemId));
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
