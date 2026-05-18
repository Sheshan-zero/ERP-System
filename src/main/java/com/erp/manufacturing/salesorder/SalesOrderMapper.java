package com.erp.manufacturing.salesorder;

import com.erp.manufacturing.salesorder.dto.PaymentResponse;
import com.erp.manufacturing.salesorder.dto.SalesOrderItemResponse;
import com.erp.manufacturing.salesorder.dto.SalesOrderRequest;
import com.erp.manufacturing.salesorder.dto.SalesOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SalesOrderMapper {

    public SalesOrder toEntity(SalesOrderRequest request) {
        SalesOrder salesOrder = SalesOrder.builder()
                .customerId(request.customerId())
                .employeeId(request.employeeId())
                .orderDate(request.orderDate())
                .orderStatus(request.orderStatus())
                .salesOrderItems(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();

        if (request.salesOrderItems() != null) {
            request.salesOrderItems().forEach(item -> salesOrder.getSalesOrderItems().add(
                    SalesOrderItem.builder()
                            .salesOrderItemId(item.salesOrderItemId())
                            .salesOrder(salesOrder)
                            .finishedProductId(item.finishedProductId())
                            .quantity(item.quantity())
                            .unitPrice(item.unitPrice())
                            .build()
            ));
        }
        if (request.payments() != null) {
            request.payments().forEach(payment -> salesOrder.getPayments().add(
                    Payment.builder()
                            .paymentId(payment.paymentId())
                            .salesOrder(salesOrder)
                            .paymentDate(payment.paymentDate())
                            .amount(payment.amount())
                            .paymentMethod(payment.paymentMethod())
                            .paymentStatus(payment.paymentStatus())
                            .build()
            ));
        }

        return salesOrder;
    }

    public SalesOrderResponse toResponse(SalesOrder salesOrder) {
        List<SalesOrderItemResponse> items = salesOrder.getSalesOrderItems().stream()
                .map(item -> new SalesOrderItemResponse(
                        item.getSalesOrderItemId(),
                        item.getFinishedProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .toList();
        List<PaymentResponse> payments = salesOrder.getPayments().stream()
                .map(payment -> new PaymentResponse(
                        payment.getPaymentId(),
                        payment.getPaymentDate(),
                        payment.getAmount(),
                        payment.getPaymentMethod(),
                        payment.getPaymentStatus()
                ))
                .toList();

        return new SalesOrderResponse(
                salesOrder.getSalesOrderId(),
                salesOrder.getCustomerId(),
                salesOrder.getEmployeeId(),
                salesOrder.getOrderDate(),
                salesOrder.getOrderStatus(),
                salesOrder.getTotalAmount(),
                items,
                payments
        );
    }

    public Page<SalesOrderResponse> toResponsePage(Page<SalesOrder> page) {
        return page.map(this::toResponse);
    }
}
