package com.erp.manufacturing.billofmaterial;

import com.erp.manufacturing.item.Item;
import com.erp.manufacturing.item.ItemRepository;
import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillOfMaterialService {

    private static final String FINISHED_PRODUCT_TYPE = "FinishedProduct";
    private static final String RAW_MATERIAL_TYPE = "RawMaterial";

    private final BillOfMaterialRepository billOfMaterialRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<BillOfMaterial> getAllBillOfMaterials() {
        return billOfMaterialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BillOfMaterial getBillOfMaterialById(Long id) {
        return billOfMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bill of material not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBillOfMaterialsByFinishedProductId(Long finishedProductId) {
        return billOfMaterialRepository.findByFinishedProductItemId(finishedProductId);
    }

    public BillOfMaterial createBillOfMaterial(BillOfMaterial billOfMaterial) {
        if (billOfMaterial.getBomId() != null && billOfMaterialRepository.existsById(billOfMaterial.getBomId())) {
            throw new BusinessException("Bill of material already exists with id: " + billOfMaterial.getBomId());
        }

        validateAndAttachItems(billOfMaterial);
        return billOfMaterialRepository.save(billOfMaterial);
    }

    public BillOfMaterial updateBillOfMaterial(Long id, BillOfMaterial billOfMaterial) {
        BillOfMaterial existingBillOfMaterial = getBillOfMaterialById(id);
        validateAndAttachItems(billOfMaterial);

        existingBillOfMaterial.setFinishedProduct(billOfMaterial.getFinishedProduct());
        existingBillOfMaterial.setRawMaterial(billOfMaterial.getRawMaterial());
        existingBillOfMaterial.setRequiredQuantity(billOfMaterial.getRequiredQuantity());
        existingBillOfMaterial.setWastagePercentage(billOfMaterial.getWastagePercentage());

        return billOfMaterialRepository.save(existingBillOfMaterial);
    }

    public void deleteBillOfMaterial(Long id) {
        if (!billOfMaterialRepository.existsById(id)) {
            throw new EntityNotFoundException("Bill of material not found with id: " + id);
        }

        billOfMaterialRepository.deleteById(id);
    }

    private void validateAndAttachItems(BillOfMaterial billOfMaterial) {
        validateQuantities(billOfMaterial);

        Long finishedProductId = getItemId(billOfMaterial.getFinishedProduct(), "Finished product is required");
        Long rawMaterialId = getItemId(billOfMaterial.getRawMaterial(), "Raw material is required");

        Item finishedProduct = itemRepository.findById(finishedProductId)
                .orElseThrow(() -> new EntityNotFoundException("Finished product not found with id: " + finishedProductId));
        Item rawMaterial = itemRepository.findById(rawMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("Raw material not found with id: " + rawMaterialId));

        if (!FINISHED_PRODUCT_TYPE.equals(finishedProduct.getItemType())) {
            throw new BusinessException("Finished product must reference an item with item_type = 'FinishedProduct'");
        }
        if (!RAW_MATERIAL_TYPE.equals(rawMaterial.getItemType())) {
            throw new BusinessException("Raw material must reference an item with item_type = 'RawMaterial'");
        }

        billOfMaterial.setFinishedProduct(finishedProduct);
        billOfMaterial.setRawMaterial(rawMaterial);
    }

    private void validateQuantities(BillOfMaterial billOfMaterial) {
        if (billOfMaterial.getRequiredQuantity() == null
                || billOfMaterial.getRequiredQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Required quantity must be greater than 0");
        }
        if (billOfMaterial.getWastagePercentage() != null
                && billOfMaterial.getWastagePercentage().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Wastage percentage cannot be negative");
        }
    }

    private Long getItemId(Item item, String message) {
        if (item == null || item.getItemId() == null) {
            throw new BusinessException(message);
        }

        return item.getItemId();
    }
}
