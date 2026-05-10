package com.erp.manufacturing.warehouse;

import com.erp.manufacturing.common.DtoMapper;
import com.erp.manufacturing.warehouse.dto.WarehouseRequest;
import com.erp.manufacturing.warehouse.dto.WarehouseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<Page<WarehouseResponse>> getAllWarehouses(Pageable pageable) {
        return ResponseEntity.ok(dtoMapper.mapPage(warehouseService.getAllWarehouses(pageable), WarehouseResponse.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(dtoMapper.map(warehouseService.getWarehouseById(id), WarehouseResponse.class));
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        Warehouse warehouse = dtoMapper.map(request, Warehouse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.map(warehouseService.createWarehouse(warehouse), WarehouseResponse.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        Warehouse warehouse = dtoMapper.map(request, Warehouse.class);
        return ResponseEntity.ok(dtoMapper.map(warehouseService.updateWarehouse(id, warehouse), WarehouseResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
