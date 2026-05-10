package com.erp.manufacturing.inventorytransaction;

import com.erp.manufacturing.inventorytransaction.dto.WarehouseStockDto;
import com.erp.manufacturing.inventorytransaction.dto.InventoryTransactionRequest;
import com.erp.manufacturing.inventorytransaction.dto.InventoryTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;
    private final InventoryTransactionMapper inventoryTransactionMapper;

    @GetMapping
    public ResponseEntity<Page<InventoryTransactionResponse>> getAllInventoryTransactions(Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionMapper.toResponsePage(
                inventoryTransactionService.getAllInventoryTransactions(pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryTransactionResponse> getInventoryTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryTransactionMapper.toResponse(
                inventoryTransactionService.getInventoryTransactionById(id)
        ));
    }

    @GetMapping("/stock-by-warehouse")
    public ResponseEntity<List<WarehouseStockDto>> getStockByWarehouse() {
        return ResponseEntity.ok(inventoryTransactionService.getStockByWarehouse());
    }

    @PostMapping
    public ResponseEntity<InventoryTransactionResponse> createInventoryTransaction(
            @Valid @RequestBody InventoryTransactionRequest request
    ) {
        InventoryTransaction inventoryTransaction = inventoryTransactionMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryTransactionMapper.toResponse(
                        inventoryTransactionService.createInventoryTransaction(inventoryTransaction)
                ));
    }

}
