package com.erp.manufacturing.accounting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GENERALLEDGERENTRY")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LEDGER_ENTRY_ID", nullable = false)
    private Long ledgerEntryId;

    @NotBlank(message = "Account code is required")
    @Size(max = 30, message = "Account code must not exceed 30 characters")
    @Column(name = "ACCOUNT_CODE", length = 30, nullable = false)
    private String accountCode;

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    @Column(name = "ACCOUNT_NAME", length = 100, nullable = false)
    private String accountName;

    @NotNull(message = "Debit amount is required")
    @DecimalMin(value = "0.00", message = "Debit amount cannot be negative")
    @Column(name = "DEBIT_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal debitAmount;

    @NotNull(message = "Credit amount is required")
    @DecimalMin(value = "0.00", message = "Credit amount cannot be negative")
    @Column(name = "CREDIT_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal creditAmount;

    @NotNull(message = "Entry date is required")
    @Column(name = "ENTRY_DATE", nullable = false)
    private LocalDateTime entryDate;

    @Size(max = 100, message = "Source table must not exceed 100 characters")
    @Column(name = "SOURCE_TABLE", length = 100)
    private String sourceTable;

    @Column(name = "SOURCE_ID")
    private Long sourceId;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
