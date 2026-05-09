package com.erp.manufacturing.billofmaterial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {

    List<BillOfMaterial> findByFinishedProductItemId(Long finishedProductId);
}
