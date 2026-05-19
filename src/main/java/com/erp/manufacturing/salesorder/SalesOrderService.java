package com.erp.manufacturing.salesorder;

import com.erp.manufacturing.auditlog.AuditLog;
import com.erp.manufacturing.auditlog.AuditLogRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.EntityLookupService;
import com.erp.manufacturing.common.constants.DatabaseTableNames;
import com.erp.manufacturing.common.enums.AuditActionType;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.enums.SalesOrderStatus;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.inventorytransaction.InventoryTransaction;
import com.erp.manufacturing.inventorytransaction.InventoryTransactionRepository;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.salesorder.dto.SalesInvoiceDto;
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
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ItemStockService itemStockService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final EntityLookupService entityLookupService;

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
        validateModifiable(existingSalesOrder);

        existingSalesOrder.setCustomerId(salesOrder.getCustomerId());
        existingSalesOrder.setEmployeeId(salesOrder.getEmployeeId());
        existingSalesOrder.setOrderDate(salesOrder.getOrderDate());
        existingSalesOrder.setOrderStatus(salesOrder.getOrderStatus());

        mergeSalesOrderItems(existingSalesOrder, salesOrder.getSalesOrderItems());
        existingSalesOrder.setTotalAmount(calculateTotalAmount(existingSalesOrder.getSalesOrderItems()));

        mergePayments(existingSalesOrder, salesOrder.getPayments());

        return salesOrderRepository.save(existingSalesOrder);
    }

    public void deleteSalesOrder(Long id) {
        SalesOrder salesOrder = getSalesOrderById(id);
        validateModifiable(salesOrder);
        salesOrderRepository.delete(salesOrder);
    }

    @Transactional(readOnly = true)
    public SalesInvoiceDto generateInvoice(Long salesOrderId) {
        SalesOrder salesOrder = getSalesOrderById(salesOrderId);
        BigDecimal totalAmount = salesOrder.getTotalAmount() == null ? BigDecimal.ZERO : salesOrder.getTotalAmount();
        BigDecimal paidAmount = salesOrder.getPayments().stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceAmount = totalAmount.subtract(paidAmount);

        return new SalesInvoiceDto(
                salesOrder.getSalesOrderId(),
                salesOrder.getSalesOrderId(),
                salesOrder.getCustomerId(),
                salesOrder.getOrderDate(),
                totalAmount,
                paidAmount,
                balanceAmount,
                balanceAmount.compareTo(BigDecimal.ZERO) <= 0 ? "Paid" : "Open"
        );
    }

    public SalesOrder deliverSalesOrder(Long salesOrderId, Long warehouseId) {
        SalesOrder salesOrder = getSalesOrderById(salesOrderId);

        if (salesOrder.getOrderStatus() == SalesOrderStatus.Delivered) {
            throw new BusinessException("Sales order is already delivered");
        }
        if (salesOrder.getOrderStatus() == SalesOrderStatus.Cancelled) {
            throw new BusinessException("Cancelled sales orders cannot be delivered");
        }

        Employee employee = entityLookupService.getEmployeeReference(salesOrder.getEmployeeId());
        Warehouse warehouse = entityLookupService.getRequiredWarehouseReference(
                warehouseId,
                "Warehouse ID is required to deliver sales order stock"
        );
        LocalDateTime now = LocalDateTime.now();

        for (SalesOrderItem salesOrderItem : salesOrder.getSalesOrderItems()) {
            Item finishedProduct = entityLookupService.getItem(
                    salesOrderItem.getFinishedProductId(),
                    "Finished product not found with id: "
            );
            BigDecimal quantity = entityLookupService.requirePositiveQuantity(
                    salesOrderItem.getQuantity(),
                    "Sales order item quantity must be greater than 0"
            );
            Item updatedFinishedProduct = itemStockService.decreaseStock(finishedProduct.getItemId(), warehouseId, quantity);
            inventoryTransactionRepository.save(InventoryTransaction.builder()
                    .item(updatedFinishedProduct)
                    .warehouse(warehouse)
                    .employee(employee)
                    .transactionType(InventoryTransactionType.StockOut.getValue())
                    .quantity(quantity)
                    .transactionDate(now)
                    .remarks("Sales order " + salesOrderId + " delivered")
                    .build());
        }

        salesOrder.setOrderStatus(SalesOrderStatus.Delivered);

        auditLogRepository.save(AuditLog.builder()
                .employee(employee)
                .tableName(DatabaseTableNames.SALES_ORDER)
                .actionType(AuditActionType.DELIVER.name())
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

    private void calculateLineTotal(SalesOrderItem item) {
        BigDecimal quantity = entityLookupService.requirePositiveQuantity(
                item.getQuantity(),
                "Sales order item quantity must be greater than 0"
        );
        BigDecimal unitPrice = entityLookupService.requirePositiveQuantity(
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

    private void validateModifiable(SalesOrder salesOrder) {
        if (salesOrder.getOrderStatus() != null && salesOrder.getOrderStatus() != SalesOrderStatus.Pending) {
            throw new BusinessException("Cannot modify a sales order that is already "
                    + salesOrder.getOrderStatus());
        }
    }

    private void mergeSalesOrderItems(SalesOrder salesOrder, List<SalesOrderItem> incomingItems) {
        List<SalesOrderItem> requestedItems = incomingItems == null ? List.of() : incomingItems;
        Map<Long, SalesOrderItem> existingById = salesOrder.getSalesOrderItems().stream()
                .filter(item -> item.getSalesOrderItemId() != null)
                .collect(Collectors.toMap(SalesOrderItem::getSalesOrderItemId, Function.identity()));

        salesOrder.getSalesOrderItems().removeIf(existing ->
                existing.getSalesOrderItemId() != null
                        && requestedItems.stream()
                        .map(SalesOrderItem::getSalesOrderItemId)
                        .filter(Objects::nonNull)
                        .noneMatch(existing.getSalesOrderItemId()::equals)
        );

        for (SalesOrderItem incoming : requestedItems) {
            SalesOrderItem target;
            if (incoming.getSalesOrderItemId() == null) {
                target = new SalesOrderItem();
                target.setSalesOrder(salesOrder);
                salesOrder.getSalesOrderItems().add(target);
            } else {
                target = existingById.get(incoming.getSalesOrderItemId());
                if (target == null) {
                    throw new BusinessException("Sales order item does not belong to this order: "
                            + incoming.getSalesOrderItemId());
                }
            }

            target.setFinishedProductId(incoming.getFinishedProductId());
            target.setQuantity(incoming.getQuantity());
            target.setUnitPrice(incoming.getUnitPrice());
            calculateLineTotal(target);
        }
    }

    private void mergePayments(SalesOrder salesOrder, List<Payment> incomingPayments) {
        List<Payment> requestedPayments = incomingPayments == null ? List.of() : incomingPayments;
        Map<Long, Payment> existingById = salesOrder.getPayments().stream()
                .filter(payment -> payment.getPaymentId() != null)
                .collect(Collectors.toMap(Payment::getPaymentId, Function.identity()));

        salesOrder.getPayments().removeIf(existing ->
                existing.getPaymentId() != null
                        && requestedPayments.stream()
                        .map(Payment::getPaymentId)
                        .filter(Objects::nonNull)
                        .noneMatch(existing.getPaymentId()::equals)
        );

        for (Payment incoming : requestedPayments) {
            Payment target;
            if (incoming.getPaymentId() == null) {
                target = new Payment();
                target.setSalesOrder(salesOrder);
                salesOrder.getPayments().add(target);
            } else {
                target = existingById.get(incoming.getPaymentId());
                if (target == null) {
                    throw new BusinessException("Payment does not belong to this sales order: "
                            + incoming.getPaymentId());
                }
            }

            target.setPaymentDate(incoming.getPaymentDate());
            target.setAmount(incoming.getAmount());
            target.setPaymentMethod(incoming.getPaymentMethod());
            target.setPaymentStatus(incoming.getPaymentStatus());
        }
    }
}
