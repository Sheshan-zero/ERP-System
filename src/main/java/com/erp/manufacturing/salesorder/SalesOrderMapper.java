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
                .customerId(request.getCustomerId())
                .employeeId(request.getEmployeeId())
                .orderDate(request.getOrderDate())
                .orderStatus(request.getOrderStatus())
                .salesOrderItems(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();

        if (request.getSalesOrderItems() != null) {
            request.getSalesOrderItems().forEach(item -> salesOrder.getSalesOrderItems().add(
                    SalesOrderItem.builder()
                            .salesOrderItemId(item.getSalesOrderItemId())
                            .salesOrder(salesOrder)
                            .finishedProductId(item.getFinishedProductId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .build()
            ));
        }
        if (request.getPayments() != null) {
            request.getPayments().forEach(payment -> salesOrder.getPayments().add(
                    Payment.builder()
                            .paymentId(payment.getPaymentId())
                            .salesOrder(salesOrder)
                            .paymentDate(payment.getPaymentDate())
                            .amount(payment.getAmount())
                            .paymentMethod(payment.getPaymentMethod())
                            .paymentStatus(payment.getPaymentStatus())
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
