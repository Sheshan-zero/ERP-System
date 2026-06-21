package com.erp.manufacturing.accounting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralLedgerEntryRepository extends JpaRepository<GeneralLedgerEntry, Long> {
}
