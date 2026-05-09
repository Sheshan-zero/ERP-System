package com.erp.manufacturing.production;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.Map;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
@Tag(name = "Production Orders", description = "Manage production orders and completion workflow")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @GetMapping
    @Operation(summary = "Get all production orders")
    @ApiResponse(responseCode = "200", description = "Production orders retrieved successfully")
    public ResponseEntity<Page<ProductionOrder>> getAllProductionOrders(Pageable pageable) {
        return ResponseEntity.ok(productionOrderService.getAllProductionOrders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get production order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Production order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Production order not found")
    })
    public ResponseEntity<ProductionOrder> getProductionOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(productionOrderService.getProductionOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create production order", description = "Creates a production order with optional material usage and assignments.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Production order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid production order request")
    })
    public ResponseEntity<ProductionOrder> createProductionOrder(
            @Valid @RequestBody ProductionOrder productionOrder
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productionOrderService.createProductionOrder(productionOrder));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update production order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Production order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Production order not found")
    })
    public ResponseEntity<ProductionOrder> updateProductionOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionOrder productionOrder
    ) {
        return ResponseEntity.ok(productionOrderService.updateProductionOrder(id, productionOrder));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete production order", description = "Completes production, updates stock, and creates audit and inventory records.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Production order completed successfully"),
            @ApiResponse(responseCode = "400", description = "Production order cannot be completed"),
            @ApiResponse(responseCode = "404", description = "Production order not found")
    })
    public ResponseEntity<Map<String, Object>> completeProductionOrder(@PathVariable Long id) {
        ProductionOrder completedProductionOrder = productionOrderService.completeProductionOrder(id);

        return ResponseEntity.ok(Map.of(
                "message", "Production order completed successfully",
                "productionOrder", completedProductionOrder
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete production order")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Production order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Production order not found")
    })
    public ResponseEntity<Void> deleteProductionOrder(@PathVariable Long id) {
        productionOrderService.deleteProductionOrder(id);
        return ResponseEntity.noContent().build();
    }
}
