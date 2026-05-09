package com.erp.manufacturing.inventorytransaction;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Transactional(readOnly = true)
    public List<InventoryTransaction> getAllInventoryTransactions() {
        return inventoryTransactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public InventoryTransaction getInventoryTransactionById(Long id) {
        return inventoryTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory transaction not found with id: " + id));
    }

    public InventoryTransaction createInventoryTransaction(InventoryTransaction inventoryTransaction) {
        if (inventoryTransaction.getTransactionId() != null
                && inventoryTransactionRepository.existsById(inventoryTransaction.getTransactionId())) {
            throw new IllegalArgumentException("Inventory transaction already exists with id: "
                    + inventoryTransaction.getTransactionId());
        }

        return inventoryTransactionRepository.save(inventoryTransaction);
    }

    public InventoryTransaction updateInventoryTransaction(Long id, InventoryTransaction inventoryTransaction) {
        InventoryTransaction existingInventoryTransaction = getInventoryTransactionById(id);

        existingInventoryTransaction.setItem(inventoryTransaction.getItem());
        existingInventoryTransaction.setWarehouse(inventoryTransaction.getWarehouse());
        existingInventoryTransaction.setEmployee(inventoryTransaction.getEmployee());
        existingInventoryTransaction.setTransactionType(inventoryTransaction.getTransactionType());
        existingInventoryTransaction.setQuantity(inventoryTransaction.getQuantity());
        existingInventoryTransaction.setTransactionDate(inventoryTransaction.getTransactionDate());
        existingInventoryTransaction.setRemarks(inventoryTransaction.getRemarks());

        return inventoryTransactionRepository.save(existingInventoryTransaction);
    }

    public void deleteInventoryTransaction(Long id) {
        if (!inventoryTransactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Inventory transaction not found with id: " + id);
        }

        inventoryTransactionRepository.deleteById(id);
    }
}
