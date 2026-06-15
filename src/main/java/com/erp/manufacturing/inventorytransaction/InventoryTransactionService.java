package com.erp.manufacturing.inventorytransaction;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.enums.InventoryTransactionType;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.inventorytransaction.dto.WarehouseStockDto;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import com.erp.manufacturing.item.ItemStockService;
import com.erp.manufacturing.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ItemRepository itemRepository;
    private final ItemStockService itemStockService;
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

        if (inventoryTransaction.getWarehouse() == null || inventoryTransaction.getWarehouse().getWarehouseId() == null) {
            throw new BusinessException("Warehouse ID is required for inventory transactions");
        }

        if (inventoryTransaction.getItem() != null && inventoryTransaction.getItem().getItemId() != null) {
            Long itemId = inventoryTransaction.getItem().getItemId();
            Long warehouseId = inventoryTransaction.getWarehouse().getWarehouseId();
            Item item = itemRepository.findById(inventoryTransaction.getItem().getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item not found with id: " + inventoryTransaction.getItem().getItemId()
                    ));

            if (InventoryTransactionType.StockOut.getValue().equalsIgnoreCase(inventoryTransaction.getTransactionType())) {
                item = itemStockService.decreaseStock(itemId, warehouseId, inventoryTransaction.getQuantity());
            } else if (InventoryTransactionType.StockIn.getValue().equalsIgnoreCase(inventoryTransaction.getTransactionType())) {
                item = itemStockService.increaseStock(itemId, warehouseId, inventoryTransaction.getQuantity());
            } else {
                throw new BusinessException("Unsupported inventory transaction type: "
                        + inventoryTransaction.getTransactionType());
            }

            inventoryTransaction.setItem(item);
            inventoryTransaction.setWarehouse(entityManager.getReference(Warehouse.class, warehouseId));
        }
        if (inventoryTransaction.getTransactionDate() == null) {
            inventoryTransaction.setTransactionDate(LocalDateTime.now());
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
                               ib.quantity_on_hand
                        FROM inventorybalance ib
                        JOIN item i ON i.item_id = ib.item_id
                        JOIN warehouse w ON w.warehouse_id = ib.warehouse_id
                        ORDER BY i.item_name, w.warehouse_name
                        """)
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
