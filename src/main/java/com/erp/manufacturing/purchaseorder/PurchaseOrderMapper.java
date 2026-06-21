package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.purchaseorder.dto.PurchaseOrderItemResponse;
import com.erp.manufacturing.purchaseorder.dto.PurchaseOrderRequest;
import com.erp.manufacturing.purchaseorder.dto.PurchaseOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrder toEntity(PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .employeeId(request.getEmployeeId())
                .orderDate(request.getOrderDate())
                .expectedDate(request.getExpectedDate())
                .status(request.getStatus())
                .purchaseOrderItems(new ArrayList<>())
                .build();

        if (request.getPurchaseOrderItems() != null) {
            request.getPurchaseOrderItems().forEach(itemRequest -> purchaseOrder.getPurchaseOrderItems().add(
                    PurchaseOrderItem.builder()
                            .purchaseOrderItemId(itemRequest.getPurchaseOrderItemId())
                            .purchaseOrder(purchaseOrder)
                            .rawMaterialId(itemRequest.getRawMaterialId())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .build()
            ));
        }

        return purchaseOrder;
    }

    public PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderItemResponse> items = purchaseOrder.getPurchaseOrderItems().stream()
                .map(item -> new PurchaseOrderItemResponse(
                        item.getPurchaseOrderItemId(),
                        item.getRawMaterialId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .toList();

        return new PurchaseOrderResponse(
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getEmployeeId(),
                purchaseOrder.getOrderDate(),
                purchaseOrder.getExpectedDate(),
                purchaseOrder.getStatus(),
                purchaseOrder.getTotalAmount(),
                items
        );
    }

    public Page<PurchaseOrderResponse> toResponsePage(Page<PurchaseOrder> page) {
        return page.map(this::toResponse);
    }
}
