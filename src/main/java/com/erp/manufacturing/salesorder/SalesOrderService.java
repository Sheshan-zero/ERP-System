package com.erp.manufacturing.salesorder;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;

    @Transactional(readOnly = true)
    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SalesOrder getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found with id: " + id));
    }

    public SalesOrder createSalesOrder(SalesOrder salesOrder) {
        if (salesOrder.getSalesOrderId() != null && salesOrderRepository.existsById(salesOrder.getSalesOrderId())) {
            throw new IllegalArgumentException("Sales order already exists with id: " + salesOrder.getSalesOrderId());
        }

        attachChildren(salesOrder);
        return salesOrderRepository.save(salesOrder);
    }

    public SalesOrder updateSalesOrder(Long id, SalesOrder salesOrder) {
        SalesOrder existingSalesOrder = getSalesOrderById(id);

        existingSalesOrder.setCustomerId(salesOrder.getCustomerId());
        existingSalesOrder.setEmployeeId(salesOrder.getEmployeeId());
        existingSalesOrder.setOrderDate(salesOrder.getOrderDate());
        existingSalesOrder.setOrderStatus(salesOrder.getOrderStatus());
        existingSalesOrder.setTotalAmount(salesOrder.getTotalAmount());

        existingSalesOrder.getSalesOrderItems().clear();
        if (salesOrder.getSalesOrderItems() != null) {
            for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
                item.setSalesOrder(existingSalesOrder);
                existingSalesOrder.getSalesOrderItems().add(item);
            }
        }

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
            throw new EntityNotFoundException("Sales order not found with id: " + id);
        }

        salesOrderRepository.deleteById(id);
    }

    private void attachChildren(SalesOrder salesOrder) {
        if (salesOrder.getSalesOrderItems() == null) {
            salesOrder.setSalesOrderItems(new ArrayList<>());
        }
        if (salesOrder.getPayments() == null) {
            salesOrder.setPayments(new ArrayList<>());
        }

        for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
            item.setSalesOrder(salesOrder);
        }
        for (Payment payment : salesOrder.getPayments()) {
            payment.setSalesOrder(salesOrder);
        }
    }
}
