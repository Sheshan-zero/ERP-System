package com.erp.manufacturing.common;

import com.erp.manufacturing.employee.Employee;
import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import com.erp.manufacturing.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EntityLookupService {

    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    public Item getItem(Long itemId, String messagePrefix) {
        if (itemId == null) {
            throw new BusinessException(messagePrefix + "null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(messagePrefix + itemId));
    }

    public Employee getEmployeeReference(Long employeeId) {
        if (employeeId == null) {
            return null;
        }

        return entityManager.getReference(Employee.class, employeeId);
    }

    public Warehouse getRequiredWarehouseReference(Long warehouseId, String message) {
        if (warehouseId == null) {
            throw new BusinessException(message);
        }

        return entityManager.getReference(Warehouse.class, warehouseId);
    }

    public BigDecimal requirePositiveQuantity(BigDecimal quantity, String message) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(message);
        }

        return quantity;
    }
}
