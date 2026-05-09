package com.erp.manufacturing.salesorder;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.enums.SalesOrderStatus;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
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
public class SalesOrderService {

    private static final String STOCK_OUT_TRANSACTION_TYPE = "Stock Out";

    private final SalesOrderRepository salesOrderRepository;
    private final ItemRepository itemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<SalesOrder> getAllSalesOrders(Pageable pageable) {
        return salesOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public SalesOrder getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));
    }

    public SalesOrder createSalesOrder(SalesOrder salesOrder) {
        if (salesOrder.getSalesOrderId() != null && salesOrderRepository.existsById(salesOrder.getSalesOrderId())) {
            throw new BusinessException("Sales order already exists with id: " + salesOrder.getSalesOrderId());
        }

        attachChildrenAndCalculateTotals(salesOrder);
        return salesOrderRepository.save(salesOrder);
    }

    public SalesOrder updateSalesOrder(Long id, SalesOrder salesOrder) {
        SalesOrder existingSalesOrder = getSalesOrderById(id);

        existingSalesOrder.setCustomerId(salesOrder.getCustomerId());
        existingSalesOrder.setEmployeeId(salesOrder.getEmployeeId());
        existingSalesOrder.setOrderDate(salesOrder.getOrderDate());
        existingSalesOrder.setOrderStatus(salesOrder.getOrderStatus());

        existingSalesOrder.getSalesOrderItems().clear();
        if (salesOrder.getSalesOrderItems() != null) {
            for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
                item.setSalesOrder(existingSalesOrder);
                calculateLineTotal(item);
                existingSalesOrder.getSalesOrderItems().add(item);
            }
        }
        existingSalesOrder.setTotalAmount(calculateTotalAmount(existingSalesOrder.getSalesOrderItems()));

        existingSalesOrder.getPayments().clear();
        if (salesOrder.getPayments() != null) {
            for (Payment payment : salesOrder.getPayments()) {
                payment.setSalesOrder(existingSalesOrder);
                existingSalesOrder.getPayments().add(payment);
            }
        }

        return salesOrderRepository.save(existingSalesOrder);
    }

    public void deleteSalesOrder(Long id) {
        if (!salesOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sales order not found with id: " + id);
        }

        salesOrderRepository.deleteById(id);
    }

    public SalesOrder deliverSalesOrder(Long salesOrderId) {
        SalesOrder salesOrder = getSalesOrderById(salesOrderId);

        if (salesOrder.getOrderStatus() == SalesOrderStatus.Delivered) {
            throw new BusinessException("Sales order is already delivered");
        }
        if (salesOrder.getOrderStatus() == SalesOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled sales orders cannot be delivered");
        }

        Employee employee = getEmployeeReference(salesOrder.getEmployeeId());
        LocalDateTime now = LocalDateTime.now();

        for (SalesOrderItem salesOrderItem : salesOrder.getSalesOrderItems()) {
            Item finishedProduct = getItem(
                    salesOrderItem.getFinishedProductId(),
                    "Finished product not found with id: "
            );
            BigDecimal quantity = requirePositiveQuantity(
                    salesOrderItem.getQuantity(),
                    "Sales order item quantity must be greater than 0"
            );
            ensureStockAvailable(finishedProduct, quantity);

            finishedProduct.setCurrentStock(getCurrentStock(finishedProduct).subtract(quantity));
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(finishedProduct)
                    .employee(employee)
                    .transactionType(STOCK_OUT_TRANSACTION_TYPE)
                    .quantity(quantity)
                    .transactionDate(now)
                    .remarks("Sales order " + salesOrderId + " delivered")
                    .build());
        }

        salesOrder.setOrderStatus(SalesOrderStatus.Delivered);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName("SALESORDER")
                .actionType("DELIVER")
                .recordId(salesOrderId)
                .actionDate(now)
                .description("Delivered sales order " + salesOrderId)
                .build());

        return salesOrderRepository.save(salesOrder);
    }

    private void attachChildrenAndCalculateTotals(SalesOrder salesOrder) {
        if (salesOrder.getSalesOrderItems() == null) {
            salesOrder.setSalesOrderItems(new ArrayList<>());
        }
        if (salesOrder.getPayments() == null) {
            salesOrder.setPayments(new ArrayList<>());
        }

        for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
            item.setSalesOrder(salesOrder);
            calculateLineTotal(item);
        }
        for (Payment payment : salesOrder.getPayments()) {
            payment.setSalesOrder(salesOrder);
        }
        salesOrder.setTotalAmount(calculateTotalAmount(salesOrder.getSalesOrderItems()));
    }

    private Item getItem(Long itemId, String messagePrefix) {
        if (itemId == null) {
            throw new BusinessException(messagePrefix + "null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(messagePrefix + itemId));
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
            throw new BusinessException(message);
        }

        return quantity;
    }

    private void ensureStockAvailable(Item item, BigDecimal quantity) {
        if (getCurrentStock(item).compareTo(quantity) < 0) {
            throw new BusinessException("Insufficient stock for item id: " + item.getItemId());
        }
    }

    private void calculateLineTotal(SalesOrderItem item) {
        BigDecimal quantity = requirePositiveQuantity(
                item.getQuantity(),
                "Sales order item quantity must be greater than 0"
        );
        BigDecimal unitPrice = requirePositiveQuantity(
                item.getUnitPrice(),
                "Sales order item unit price must be greater than 0"
        );

        item.setLineTotal(quantity.multiply(unitPrice));
    }

    private BigDecimal calculateTotalAmount(List<SalesOrderItem> items) {
        return items.stream()
                .map(SalesOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
