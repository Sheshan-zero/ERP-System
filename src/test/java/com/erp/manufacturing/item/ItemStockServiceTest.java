package com.erp.manufacturing.item;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.inventorybalance.InventoryBalanceService;
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
class ItemStockServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryBalanceService inventoryBalanceService;

    @InjectMocks
    private ItemStockService itemStockService;

    @Test
    void decreaseStockRejectsNegativeResultBeforeSaving() {
        Item item = Item.builder()
                .itemId(10L)
                .currentStock(new BigDecimal("5.00"))
                .build();
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemStockService.decreaseStock(10L, 1L, new BigDecimal("6.00")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(itemRepository, never()).save(item);
        verify(inventoryBalanceService, never()).decreaseStock(10L, 1L, new BigDecimal("6.00"));
    }

    @Test
    void increaseStockUpdatesItemAndWarehouseBalanceTogether() {
        Item item = Item.builder()
                .itemId(10L)
                .currentStock(new BigDecimal("5.00"))
                .build();
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item updatedItem = itemStockService.increaseStock(10L, 1L, new BigDecimal("4.00"));

        assertThat(updatedItem.getCurrentStock()).isEqualByComparingTo("9.00");
        verify(inventoryBalanceService).increaseStock(10L, 1L, new BigDecimal("4.00"));
    }
}
