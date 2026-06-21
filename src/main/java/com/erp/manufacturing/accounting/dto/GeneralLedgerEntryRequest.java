package com.erp.manufacturing.accounting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerEntryRequest {
    private @NotBlank @Size(max = 30) String accountCode;
    private @NotBlank @Size(max = 100) String accountName;
    private @NotNull @DecimalMin("0.00") BigDecimal debitAmount;
    private @NotNull @DecimalMin("0.00") BigDecimal creditAmount;
    private LocalDateTime entryDate;
    private @Size(max = 100) String sourceTable;
    private Long sourceId;
    private @Size(max = 255) String description;
}
