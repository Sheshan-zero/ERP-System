package com.erp.manufacturing.purchaseorder;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

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

    private void attachItems(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderItems() == null) {
            purchaseOrder.setPurchaseOrderItems(new ArrayList<>());
            return;
        }

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            item.setPurchaseOrder(purchaseOrder);
        }
    }
}
