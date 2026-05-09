package com.erp.manufacturing.production;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionAssignmentRepository extends JpaRepository<ProductionAssignment, Long> {
}
