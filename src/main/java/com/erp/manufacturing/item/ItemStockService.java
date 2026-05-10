package com.erp.manufacturing.item;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import com.erp.manufacturing.inventorybalance.InventoryBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ItemStockService {

    private final ItemRepository itemRepository;
    private final InventoryBalanceService inventoryBalanceService;

    @Transactional
    public Item increaseStock(Long itemId, BigDecimal quantity) {
        return adjustStock(itemId, requirePositiveQuantity(quantity), false);
    }

    @Transactional
    public Item increaseStock(Long itemId, Long warehouseId, BigDecimal quantity) {
        Item item = adjustStock(itemId, requirePositiveQuantity(quantity), false);
        inventoryBalanceService.increaseStock(itemId, warehouseId, quantity);
        return item;
    }

    @Transactional
    public Item decreaseStock(Long itemId, BigDecimal quantity) {
        return adjustStock(itemId, requirePositiveQuantity(quantity).negate(), true);
    }

    @Transactional
    public Item decreaseStock(Long itemId, Long warehouseId, BigDecimal quantity) {
        Item item = adjustStock(itemId, requirePositiveQuantity(quantity).negate(), true);
        inventoryBalanceService.decreaseStock(itemId, warehouseId, quantity);
        return item;
    }

    private Item adjustStock(Long itemId, BigDecimal delta, boolean requireSufficientStock) {
        if (itemId == null) {
            throw new BusinessException("Item ID is required for stock update");
        }
        if (delta == null) {
            throw new BusinessException("Stock quantity is required");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        BigDecimal currentStock = item.getCurrentStock() == null ? BigDecimal.ZERO : item.getCurrentStock();
        BigDecimal newStock = currentStock.add(delta);

        if (requireSufficientStock && newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient stock for item id: " + itemId);
        }

        item.setCurrentStock(newStock);
        return itemRepository.saveAndFlush(item);
    }

    private BigDecimal requirePositiveQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Stock quantity must be greater than 0");
        }
        return quantity;
    }
}
