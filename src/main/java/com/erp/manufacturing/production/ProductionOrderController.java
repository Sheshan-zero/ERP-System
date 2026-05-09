package com.erp.manufacturing.production;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @GetMapping
    public ResponseEntity<List<ProductionOrder>> getAllProductionOrders() {
        return ResponseEntity.ok(productionOrderService.getAllProductionOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrder> getProductionOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(productionOrderService.getProductionOrderById(id));
    }

    @PostMapping
    public ResponseEntity<ProductionOrder> createProductionOrder(
            @Valid @RequestBody ProductionOrder productionOrder
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productionOrderService.createProductionOrder(productionOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionOrder> updateProductionOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionOrder productionOrder
    ) {
        return ResponseEntity.ok(productionOrderService.updateProductionOrder(id, productionOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductionOrder(@PathVariable Long id) {
        productionOrderService.deleteProductionOrder(id);
        return ResponseEntity.noContent().build();
    }
}
