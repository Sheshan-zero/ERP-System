package com.erp.manufacturing.salesorder;

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
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Orders", description = "Manage sales orders, payments, and delivery workflow")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @GetMapping
    @Operation(summary = "Get all sales orders")
    @ApiResponse(responseCode = "200", description = "Sales orders retrieved successfully")
    public ResponseEntity<Page<SalesOrder>> getAllSalesOrders(Pageable pageable) {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sales order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sales order not found")
    })
    public ResponseEntity<SalesOrder> getSalesOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create sales order", description = "Creates a sales order with nested line items and optional payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sales order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sales order request")
    })
    public ResponseEntity<SalesOrder> createSalesOrder(@Valid @RequestBody SalesOrder salesOrder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesOrderService.createSalesOrder(salesOrder));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sales order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Sales order not found")
    })
    public ResponseEntity<SalesOrder> updateSalesOrder(
            @PathVariable Long id,
            @Valid @RequestBody SalesOrder salesOrder
    ) {
        return ResponseEntity.ok(salesOrderService.updateSalesOrder(id, salesOrder));
    }

    @PostMapping("/{id}/deliver")
    @Operation(summary = "Deliver sales order", description = "Delivers a sales order and reduces finished product stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales order delivered successfully"),
            @ApiResponse(responseCode = "400", description = "Sales order cannot be delivered"),
            @ApiResponse(responseCode = "404", description = "Sales order not found")
    })
    public ResponseEntity<Map<String, Object>> deliverSalesOrder(@PathVariable Long id) {
        SalesOrder deliveredSalesOrder = salesOrderService.deliverSalesOrder(id);

        return ResponseEntity.ok(Map.of(
                "message", "Sales order delivered successfully",
                "salesOrder", deliveredSalesOrder
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sales order")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sales order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sales order not found")
    })
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable Long id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}
