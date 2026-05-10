package com.erp.manufacturing.inventorybalance;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryBalanceServiceTest {

    @Mock
    private InventoryBalanceRepository inventoryBalanceRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private InventoryBalanceService inventoryBalanceService;

    @Test
    void decreaseStockRejectsNegativeWarehouseBalance() {
        InventoryBalance balance = InventoryBalance.builder()
                .item(Item.builder().itemId(10L).build())
                .warehouse(Warehouse.builder().warehouseId(1L).build())
                .quantityOnHand(new BigDecimal("2.00"))
                .build();
        when(inventoryBalanceRepository.findByItemItemIdAndWarehouseWarehouseId(10L, 1L))
                .thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> inventoryBalanceService.decreaseStock(10L, 1L, new BigDecimal("3.00")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient warehouse stock");

        verify(inventoryBalanceRepository, never()).save(balance);
    }

    @Test
    void increaseStockCreatesBalanceWhenMissing() {
        Item item = Item.builder().itemId(10L).build();
        Warehouse warehouse = Warehouse.builder().warehouseId(1L).build();
        when(inventoryBalanceRepository.findByItemItemIdAndWarehouseWarehouseId(10L, 1L))
                .thenReturn(Optional.empty());
        when(entityManager.getReference(Item.class, 10L)).thenReturn(item);
        when(entityManager.getReference(Warehouse.class, 1L)).thenReturn(warehouse);
        when(inventoryBalanceRepository.save(org.mockito.ArgumentMatchers.any(InventoryBalance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryBalance balance = inventoryBalanceService.increaseStock(10L, 1L, new BigDecimal("7.00"));

        assertThat(balance.getItem()).isSameAs(item);
        assertThat(balance.getWarehouse()).isSameAs(warehouse);
        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo("7.00");
    }
}
