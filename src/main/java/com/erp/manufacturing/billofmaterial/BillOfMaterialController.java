package com.erp.manufacturing.billofmaterial;

import com.erp.manufacturing.billofmaterial.dto.BillOfMaterialRequest;
import com.erp.manufacturing.billofmaterial.dto.BillOfMaterialResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/boms")
@RequiredArgsConstructor
public class BillOfMaterialController {

    private final BillOfMaterialService billOfMaterialService;
    private final BillOfMaterialMapper billOfMaterialMapper;

    @GetMapping
    public ResponseEntity<Page<BillOfMaterialResponse>> getAllBillOfMaterials(Pageable pageable) {
        return ResponseEntity.ok(billOfMaterialMapper.toResponsePage(billOfMaterialService.getAllBillOfMaterials(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillOfMaterialResponse> getBillOfMaterialById(@PathVariable Long id) {
        return ResponseEntity.ok(billOfMaterialMapper.toResponse(billOfMaterialService.getBillOfMaterialById(id)));
    }

    @GetMapping("/product/{finishedProductId}")
    public ResponseEntity<List<BillOfMaterialResponse>> getBillOfMaterialsByFinishedProductId(
            @PathVariable Long finishedProductId
    ) {
        return ResponseEntity.ok(billOfMaterialMapper.toResponseList(
                billOfMaterialService.getBillOfMaterialsByFinishedProductId(finishedProductId)
        ));
    }

    @PostMapping
    public ResponseEntity<BillOfMaterialResponse> createBillOfMaterial(@Valid @RequestBody BillOfMaterialRequest request) {
        BillOfMaterial billOfMaterial = billOfMaterialMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billOfMaterialMapper.toResponse(billOfMaterialService.createBillOfMaterial(billOfMaterial)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillOfMaterialResponse> updateBillOfMaterial(
            @PathVariable Long id,
            @Valid @RequestBody BillOfMaterialRequest request
    ) {
        BillOfMaterial billOfMaterial = billOfMaterialMapper.toEntity(request);
        return ResponseEntity.ok(billOfMaterialMapper.toResponse(billOfMaterialService.updateBillOfMaterial(id, billOfMaterial)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillOfMaterial(@PathVariable Long id) {
        billOfMaterialService.deleteBillOfMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
