package com.erp.manufacturing.inventorybalance;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryBalanceService {

    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.MANDATORY)
    public InventoryBalance increaseStock(Long itemId, Long warehouseId, BigDecimal quantity) {
        return adjustStock(itemId, warehouseId, requirePositive(quantity), false);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public InventoryBalance decreaseStock(Long itemId, Long warehouseId, BigDecimal quantity) {
        return adjustStock(itemId, warehouseId, requirePositive(quantity).negate(), true);
    }

    private InventoryBalance adjustStock(
            Long itemId,
            Long warehouseId,
            BigDecimal delta,
            boolean requireSufficientStock
    ) {
        if (itemId == null) {
            throw new BusinessException("Item ID is required for warehouse stock update");
        }
        if (warehouseId == null) {
            throw new BusinessException("Warehouse ID is required for stock movement");
        }

        InventoryBalance balance = inventoryBalanceRepository
                .findByItemItemIdAndWarehouseWarehouseId(itemId, warehouseId)
                .orElseGet(() -> InventoryBalance.builder()
                        .item(entityManager.getReference(Item.class, itemId))
                        .warehouse(entityManager.getReference(Warehouse.class, warehouseId))
                        .quantityOnHand(BigDecimal.ZERO)
                        .build());

        BigDecimal currentQuantity = balance.getQuantityOnHand() == null
                ? BigDecimal.ZERO
                : balance.getQuantityOnHand();
        BigDecimal newQuantity = currentQuantity.add(delta);

        if (requireSufficientStock && newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient warehouse stock for item id: "
                    + itemId + " in warehouse id: " + warehouseId);
        }

        balance.setQuantityOnHand(newQuantity);
        return inventoryBalanceRepository.save(balance);
    }

    private BigDecimal requirePositive(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Stock quantity must be greater than 0");
        }
        return quantity;
    }
}
