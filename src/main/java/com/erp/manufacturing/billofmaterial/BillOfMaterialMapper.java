package com.erp.manufacturing.billofmaterial;

import com.erp.manufacturing.billofmaterial.dto.BillOfMaterialRequest;
import com.erp.manufacturing.billofmaterial.dto.BillOfMaterialResponse;
import com.erp.manufacturing.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillOfMaterialMapper {

    public BillOfMaterial toEntity(BillOfMaterialRequest request) {
        return BillOfMaterial.builder()
                .finishedProduct(Item.builder().itemId(request.finishedProductId()).build())
                .rawMaterial(Item.builder().itemId(request.rawMaterialId()).build())
                .requiredQuantity(request.requiredQuantity())
                .wastagePercentage(request.wastagePercentage())
                .build();
    }

    public BillOfMaterialResponse toResponse(BillOfMaterial billOfMaterial) {
        Item finishedProduct = billOfMaterial.getFinishedProduct();
        Item rawMaterial = billOfMaterial.getRawMaterial();

        return new BillOfMaterialResponse(
                billOfMaterial.getBomId(),
                finishedProduct == null ? null : finishedProduct.getItemId(),
                finishedProduct == null ? null : finishedProduct.getItemName(),
                rawMaterial == null ? null : rawMaterial.getItemId(),
                rawMaterial == null ? null : rawMaterial.getItemName(),
                billOfMaterial.getRequiredQuantity(),
                billOfMaterial.getWastagePercentage()
        );
    }

    public Page<BillOfMaterialResponse> toResponsePage(Page<BillOfMaterial> page) {
        return page.map(this::toResponse);
    }

    public List<BillOfMaterialResponse> toResponseList(List<BillOfMaterial> billOfMaterials) {
        return billOfMaterials.stream().map(this::toResponse).toList();
    }
}
