package com.erp.manufacturing.accounting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerEntryResponse {
    private Long ledgerEntryId;
    private String accountCode;
    private String accountName;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private LocalDateTime entryDate;
    private String sourceTable;
    private Long sourceId;
    private String description;
}
