package com.erp.manufacturing.inventorytransaction;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.inventorytransaction.dto.WarehouseStockDto;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<InventoryTransaction> getAllInventoryTransactions(Pageable pageable) {
        return inventoryTransactionRepository.findAll(pageable);
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
            if (InventoryTransactionType.StockOut.getValue().equalsIgnoreCase(inventoryTransaction.getTransactionType())) {
                ensureStockAvailable(item, inventoryTransaction.getQuantity());
            }
            inventoryTransaction.setItem(item);
        }

        return inventoryTransactionRepository.save(inventoryTransaction);
    }

    @Transactional(readOnly = true)
    public List<WarehouseStockDto> getStockByWarehouse() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT i.item_id,
                               i.item_name,
                               w.warehouse_id,
                               w.warehouse_name,
                               COALESCE(SUM(CASE
                                   WHEN it.transaction_type = :stockIn THEN it.quantity
                                   WHEN it.transaction_type = :stockOut THEN -it.quantity
                                   ELSE 0
                               END), 0) AS quantity_on_hand
                        FROM inventorytransaction it
                        JOIN item i ON i.item_id = it.item_id
                        LEFT JOIN warehouse w ON w.warehouse_id = it.warehouse_id
                        GROUP BY i.item_id, i.item_name, w.warehouse_id, w.warehouse_name
                        ORDER BY i.item_name, w.warehouse_name
                        """)
                .setParameter("stockIn", InventoryTransactionType.StockIn.getValue())
                .setParameter("stockOut", InventoryTransactionType.StockOut.getValue())
                .getResultList();

        return rows.stream()
                .map(row -> new WarehouseStockDto(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2]),
                        (String) row[3],
                        toBigDecimal(row[4])
                ))
                .toList();
    }

    private void ensureStockAvailable(Item item, BigDecimal quantity) {
        BigDecimal currentStock = item.getCurrentStock() == null ? BigDecimal.ZERO : item.getCurrentStock();
        if (quantity == null || currentStock.compareTo(quantity) < 0) {
            throw new BusinessException("Insufficient stock for item id: " + item.getItemId());
        }
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        return BigDecimal.valueOf(((Number) value).doubleValue());
    }
}
