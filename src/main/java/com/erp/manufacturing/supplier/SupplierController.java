package com.erp.manufacturing.supplier;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.erp.manufacturing.common.DtoMapper;
import com.erp.manufacturing.supplier.dto.SupplierRequest;
import com.erp.manufacturing.supplier.dto.SupplierResponse;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Manage supplier master data")
public class SupplierController {

    private final SupplierService supplierService;
    private final DtoMapper dtoMapper;

    @GetMapping
    @Operation(summary = "Get all suppliers")
    @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully")
    public ResponseEntity<Page<SupplierResponse>> getAllSuppliers(Pageable pageable) {
        return ResponseEntity.ok(dtoMapper.mapPage(supplierService.getAllSuppliers(pageable), SupplierResponse.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(dtoMapper.map(supplierService.getSupplierById(id), SupplierResponse.class));
    }

    @PostMapping
    @Operation(summary = "Create supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid supplier request")
    })
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = dtoMapper.map(request, Supplier.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.map(supplierService.createSupplier(supplier), SupplierResponse.class));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        Supplier supplier = dtoMapper.map(request, Supplier.class);
        return ResponseEntity.ok(dtoMapper.map(supplierService.updateSupplier(id, supplier), SupplierResponse.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
