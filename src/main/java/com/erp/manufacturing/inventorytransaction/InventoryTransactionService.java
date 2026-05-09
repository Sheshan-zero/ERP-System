package com.erp.manufacturing.inventorytransaction;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionService {

    private static final String STOCK_OUT_TRANSACTION_TYPE = "Stock Out";

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<InventoryTransaction> getAllInventoryTransactions() {
        return inventoryTransactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public InventoryTransaction getInventoryTransactionById(Long id) {
        return inventoryTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory transaction not found with id: " + id));
    }

    public InventoryTransaction createInventoryTransaction(InventoryTransaction inventoryTransaction) {
        if (inventoryTransaction.getTransactionId() != null
                && inventoryTransactionRepository.existsById(inventoryTransaction.getTransactionId())) {
            throw new BusinessException("Inventory transaction already exists with id: "
                    + inventoryTransaction.getTransactionId());
        }

        if (inventoryTransaction.getItem() != null && inventoryTransaction.getItem().getItemId() != null) {
            Item item = itemRepository.findById(inventoryTransaction.getItem().getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item not found with id: " + inventoryTransaction.getItem().getItemId()
                    ));
            if (STOCK_OUT_TRANSACTION_TYPE.equalsIgnoreCase(inventoryTransaction.getTransactionType())) {
                ensureStockAvailable(item, inventoryTransaction.getQuantity());
            }
            inventoryTransaction.setItem(item);
        }

        return inventoryTransactionRepository.save(inventoryTransaction);
    }

    private void ensureStockAvailable(Item item, BigDecimal quantity) {
        BigDecimal currentStock = item.getCurrentStock() == null ? BigDecimal.ZERO : item.getCurrentStock();
        if (quantity == null || currentStock.compareTo(quantity) < 0) {
            throw new BusinessException("Insufficient stock for item id: " + item.getItemId());
        }
    }
}
