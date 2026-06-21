package com.erp.manufacturing.accounting;

import com.erp.manufacturing.accounting.dto.GeneralLedgerEntryRequest;
import com.erp.manufacturing.accounting.dto.GeneralLedgerEntryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/general-ledger")
@RequiredArgsConstructor
@Tag(name = "General Ledger", description = "Inspect and create general ledger entries")
public class GeneralLedgerController {

    private final GeneralLedgerService generalLedgerService;
    private final GeneralLedgerEntryMapper generalLedgerEntryMapper;

    @GetMapping
    @Operation(summary = "Get all ledger entries")
    public ResponseEntity<Page<GeneralLedgerEntryResponse>> getAllEntries(Pageable pageable) {
        return ResponseEntity.ok(generalLedgerEntryMapper.toResponsePage(generalLedgerService.getAllEntries(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ledger entry by ID")
    public ResponseEntity<GeneralLedgerEntryResponse> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(generalLedgerEntryMapper.toResponse(generalLedgerService.getEntryById(id)));
    }

    @PostMapping
    @Operation(summary = "Create manual ledger entry")
    public ResponseEntity<GeneralLedgerEntryResponse> createEntry(
            @Valid @RequestBody GeneralLedgerEntryRequest request
    ) {
        GeneralLedgerEntry entry = generalLedgerEntryMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(generalLedgerEntryMapper.toResponse(generalLedgerService.createEntry(entry)));
    }
}
