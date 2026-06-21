package com.erp.manufacturing.purchaseorder;

import com.erp.manufacturing.purchaseorder.dto.PurchaseOrderRequest;
import com.erp.manufacturing.purchaseorder.dto.PurchaseOrderResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Manage purchase orders, approval, and receiving workflow")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @GetMapping
    @Operation(summary = "Get all purchase orders")
    @ApiResponse(responseCode = "200", description = "Purchase orders retrieved successfully")
    public ResponseEntity<Page<PurchaseOrderResponse>> getAllPurchaseOrders(Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderMapper.toResponsePage(purchaseOrderService.getAllPurchaseOrders(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderMapper.toResponse(purchaseOrderService.getPurchaseOrderById(id)));
    }

    @PostMapping
    @Operation(summary = "Create purchase order", description = "Creates a purchase order with nested line items.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Purchase order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid purchase order request")
    })
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request
    ) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseOrderMapper.toResponse(purchaseOrderService.createPurchaseOrder(purchaseOrder)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update purchase order")
    public ResponseEntity<PurchaseOrderResponse> updatePurchaseOrder(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderRequest request
    ) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(request);
        return ResponseEntity.ok(purchaseOrderMapper.toResponse(purchaseOrderService.updatePurchaseOrder(id, purchaseOrder)));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve purchase order")
    public ResponseEntity<Map<String, Object>> approvePurchaseOrder(@PathVariable Long id) {
        PurchaseOrder approvedPurchaseOrder = purchaseOrderService.approvePurchaseOrder(id);
        return ResponseEntity.ok(Map.of(
                "message", "Purchase order approved successfully",
                "purchaseOrder", purchaseOrderMapper.toResponse(approvedPurchaseOrder)
        ));
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "Receive purchase order", description = "Receives raw materials and increases stock.")
    public ResponseEntity<Map<String, Object>> receivePurchaseOrder(
            @PathVariable Long id,
            @RequestParam Long warehouseId
    ) {
        PurchaseOrder receivedPurchaseOrder = purchaseOrderService.receivePurchaseOrder(id, warehouseId);
        return ResponseEntity.ok(Map.of(
                "message", "Purchase order received successfully",
                "purchaseOrder", purchaseOrderMapper.toResponse(receivedPurchaseOrder)
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete purchase order")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
