package com.erp.manufacturing.warehouse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse.getWarehouseId() != null && warehouseRepository.existsById(warehouse.getWarehouseId())) {
            throw new IllegalArgumentException("Warehouse already exists with id: " + warehouse.getWarehouseId());
        }

        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Long id, Warehouse warehouse) {
        Warehouse existingWarehouse = getWarehouseById(id);

        existingWarehouse.setWarehouseName(warehouse.getWarehouseName());
        existingWarehouse.setLocation(warehouse.getLocation());
        existingWarehouse.setCapacity(warehouse.getCapacity());
        existingWarehouse.setManagerName(warehouse.getManagerName());

        return warehouseRepository.save(existingWarehouse);
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new EntityNotFoundException("Warehouse not found with id: " + id);
        }

        warehouseRepository.deleteById(id);
    }
}
