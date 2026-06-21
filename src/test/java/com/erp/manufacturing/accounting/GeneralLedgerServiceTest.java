package com.erp.manufacturing.accounting;

import com.erp.manufacturing.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneralLedgerServiceTest {

    @Mock
    private GeneralLedgerEntryRepository generalLedgerEntryRepository;

    @InjectMocks
    private GeneralLedgerService generalLedgerService;

    @Test
    void recordBalancedEntryCreatesMatchingDebitAndCreditLines() {
        when(generalLedgerEntryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<GeneralLedgerEntry> entries = generalLedgerService.recordBalancedEntry(
                GeneralLedgerService.ACCOUNTS_RECEIVABLE,
                "Accounts Receivable",
                GeneralLedgerService.SALES_REVENUE,
                "Sales Revenue",
                new BigDecimal("2500.00"),
                "SALESORDER",
                12L,
                "Delivered sales order 12"
        );

        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getDebitAmount()).isEqualByComparingTo("2500.00");
        assertThat(entries.get(0).getCreditAmount()).isZero();
        assertThat(entries.get(1).getDebitAmount()).isZero();
        assertThat(entries.get(1).getCreditAmount()).isEqualByComparingTo("2500.00");
        assertThat(entries).allSatisfy(entry -> {
            assertThat(entry.getSourceTable()).isEqualTo("SALESORDER");
            assertThat(entry.getSourceId()).isEqualTo(12L);
            assertThat(entry.getEntryDate()).isNotNull();
        });
        verify(generalLedgerEntryRepository).saveAll(entries);
    }

    @Test
    void recordBalancedEntryRejectsNonPositiveAmount() {
        assertThatThrownBy(() -> generalLedgerService.recordBalancedEntry(
                "1000",
                "Cash",
                "4000",
                "Revenue",
                BigDecimal.ZERO,
                "MANUAL",
                1L,
                "Invalid entry"
        )).isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than 0");
    }

    @Test
    void createEntryRejectsBothDebitAndCreditAmounts() {
        GeneralLedgerEntry entry = GeneralLedgerEntry.builder()
                .accountCode("1000")
                .accountName("Cash")
                .debitAmount(BigDecimal.ONE)
                .creditAmount(BigDecimal.ONE)
                .build();

        assertThatThrownBy(() -> generalLedgerService.createEntry(entry))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("both debit and credit");
    }
}
