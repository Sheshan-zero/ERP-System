package com.erp.manufacturing.inventorytransaction;

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

    @GetMapping
    public ResponseEntity<Page<InventoryTransaction>> getAllInventoryTransactions(Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionService.getAllInventoryTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryTransaction> getInventoryTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryTransactionService.getInventoryTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryTransaction> createInventoryTransaction(
            @Valid @RequestBody InventoryTransaction inventoryTransaction
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryTransactionService.createInventoryTransaction(inventoryTransaction));
    }

}
