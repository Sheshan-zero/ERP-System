package com.erp.manufacturing.accounting;

import com.erp.manufacturing.accounting.dto.GeneralLedgerEntryRequest;
import com.erp.manufacturing.accounting.dto.GeneralLedgerEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GeneralLedgerEntryMapper {

    public GeneralLedgerEntry toEntity(GeneralLedgerEntryRequest request) {
        return GeneralLedgerEntry.builder()
                .accountCode(request.getAccountCode())
                .accountName(request.getAccountName())
                .debitAmount(request.getDebitAmount())
                .creditAmount(request.getCreditAmount())
                .entryDate(request.getEntryDate())
                .sourceTable(request.getSourceTable())
                .sourceId(request.getSourceId())
                .description(request.getDescription())
                .build();
    }

    public GeneralLedgerEntryResponse toResponse(GeneralLedgerEntry entry) {
        return new GeneralLedgerEntryResponse(
                entry.getLedgerEntryId(),
                entry.getAccountCode(),
                entry.getAccountName(),
                entry.getDebitAmount(),
                entry.getCreditAmount(),
                entry.getEntryDate(),
                entry.getSourceTable(),
                entry.getSourceId(),
                entry.getDescription()
        );
    }

    public Page<GeneralLedgerEntryResponse> toResponsePage(Page<GeneralLedgerEntry> page) {
        return page.map(this::toResponse);
    }
}
