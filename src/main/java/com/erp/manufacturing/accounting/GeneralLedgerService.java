package com.erp.manufacturing.accounting;

import com.erp.manufacturing.common.BusinessException;
import com.erp.manufacturing.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneralLedgerService {

    public static final String ACCOUNTS_PAYABLE = "2000";
    public static final String ACCOUNTS_RECEIVABLE = "1100";
    public static final String INVENTORY = "1200";
    public static final String SALES_REVENUE = "4000";

    private final GeneralLedgerEntryRepository generalLedgerEntryRepository;

    @Transactional(readOnly = true)
    public Page<GeneralLedgerEntry> getAllEntries(Pageable pageable) {
        return generalLedgerEntryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public GeneralLedgerEntry getEntryById(Long id) {
        return generalLedgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));
    }

    public GeneralLedgerEntry createEntry(GeneralLedgerEntry entry) {
        entry.setLedgerEntryId(null);
        if (entry.getEntryDate() == null) {
            entry.setEntryDate(LocalDateTime.now());
        }
        validateEntry(entry);
        return generalLedgerEntryRepository.save(entry);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<GeneralLedgerEntry> recordBalancedEntry(
            String debitAccountCode,
            String debitAccountName,
            String creditAccountCode,
            String creditAccountName,
            BigDecimal amount,
            String sourceTable,
            Long sourceId,
            String description
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Ledger amount must be greater than 0");
        }

        LocalDateTime now = LocalDateTime.now();
        GeneralLedgerEntry debit = GeneralLedgerEntry.builder()
                .accountCode(debitAccountCode)
                .accountName(debitAccountName)
                .debitAmount(amount)
                .creditAmount(BigDecimal.ZERO)
                .entryDate(now)
                .sourceTable(sourceTable)
                .sourceId(sourceId)
                .description(description)
                .build();
        GeneralLedgerEntry credit = GeneralLedgerEntry.builder()
                .accountCode(creditAccountCode)
                .accountName(creditAccountName)
                .debitAmount(BigDecimal.ZERO)
                .creditAmount(amount)
                .entryDate(now)
                .sourceTable(sourceTable)
                .sourceId(sourceId)
                .description(description)
                .build();

        return generalLedgerEntryRepository.saveAll(List.of(debit, credit));
    }

    private void validateEntry(GeneralLedgerEntry entry) {
        BigDecimal debitAmount = entry.getDebitAmount() == null ? BigDecimal.ZERO : entry.getDebitAmount();
        BigDecimal creditAmount = entry.getCreditAmount() == null ? BigDecimal.ZERO : entry.getCreditAmount();

        if (debitAmount.compareTo(BigDecimal.ZERO) > 0 && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("A ledger line cannot have both debit and credit amounts");
        }
        if (debitAmount.compareTo(BigDecimal.ZERO) == 0 && creditAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("A ledger line must have a debit or credit amount");
        }

        entry.setDebitAmount(debitAmount);
        entry.setCreditAmount(creditAmount);
    }
}
