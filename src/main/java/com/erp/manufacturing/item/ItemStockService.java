package com.erp.manufacturing.item;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ItemStockService {

    private static final int MAX_OPTIMISTIC_LOCK_RETRIES = 3;

    private final ItemRepository itemRepository;
    private final TransactionTemplate transactionTemplate;

    public Item increaseStock(Long itemId, BigDecimal quantity) {
        return adjustStockWithRetry(itemId, quantity, false);
    }

    public Item decreaseStock(Long itemId, BigDecimal quantity) {
        return adjustStockWithRetry(itemId, quantity.negate(), true);
    }

    private Item adjustStockWithRetry(Long itemId, BigDecimal delta, boolean requireSufficientStock) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_OPTIMISTIC_LOCK_RETRIES; attempt++) {
            try {
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                return transactionTemplate.execute(status -> adjustStock(itemId, delta, requireSufficientStock));
            } catch (OptimisticLockingFailureException exception) {
                lastException = exception;
            }
        }

        throw new BusinessException("Stock update failed due to concurrent changes. Please retry.");
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
}
