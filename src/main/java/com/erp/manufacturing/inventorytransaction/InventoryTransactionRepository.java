package com.erp.manufacturing.inventorytransaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @EntityGraph(attributePaths = {"item", "warehouse", "employee"})
    Page<InventoryTransaction> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"item", "warehouse", "employee"})
    Optional<InventoryTransaction> findById(Long id);
}
