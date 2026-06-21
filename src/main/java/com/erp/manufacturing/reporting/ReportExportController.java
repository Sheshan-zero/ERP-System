package com.erp.manufacturing.reporting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "CSV report and invoice exports")
public class ReportExportController {

    private static final MediaType TEXT_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final ReportExportService reportExportService;

    @GetMapping("/sales-orders/{id}/invoice.csv")
    @Operation(summary = "Export sales invoice as CSV")
    public ResponseEntity<String> exportSalesInvoice(@PathVariable Long id) {
        return csv("sales-invoice-" + id + ".csv", reportExportService.exportSalesInvoiceCsv(id));
    }

    @GetMapping("/monthly-sales-summary.csv")
    @Operation(summary = "Export monthly sales summary as CSV")
    public ResponseEntity<String> exportMonthlySalesSummary() {
        return csv("monthly-sales-summary.csv", reportExportService.exportMonthlySalesSummaryCsv());
    }

    @GetMapping("/top-selling-products.csv")
    @Operation(summary = "Export top selling products as CSV")
    public ResponseEntity<String> exportTopSellingProducts() {
        return csv("top-selling-products.csv", reportExportService.exportTopSellingProductsCsv());
    }

    @GetMapping("/supplier-purchase-summary.csv")
    @Operation(summary = "Export supplier purchase summary as CSV")
    public ResponseEntity<String> exportSupplierPurchaseSummary() {
        return csv("supplier-purchase-summary.csv", reportExportService.exportSupplierPurchaseSummaryCsv());
    }

    private ResponseEntity<String> csv(String filename, String body) {
        return ResponseEntity.ok()
                .contentType(TEXT_CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(filename)
                        .build()
                        .toString())
                .body(body);
    }
}
