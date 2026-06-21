package com.erp.manufacturing.reporting;

import com.erp.manufacturing.dashboard.DashboardService;
import com.erp.manufacturing.dashboard.dto.MonthlySalesSummaryDto;
import com.erp.manufacturing.dashboard.dto.SupplierPurchaseSummaryDto;
import com.erp.manufacturing.dashboard.dto.TopSellingProductDto;
import com.erp.manufacturing.salesorder.SalesOrderService;
import com.erp.manufacturing.salesorder.dto.SalesInvoiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportExportService {

    private final SalesOrderService salesOrderService;
    private final DashboardService dashboardService;

    public String exportSalesInvoiceCsv(Long salesOrderId) {
        SalesInvoiceDto invoice = salesOrderService.generateInvoice(salesOrderId);
        return rows(
                row("invoiceId", "salesOrderId", "customerId", "orderDate", "totalAmount", "paidAmount", "balanceAmount", "status"),
                row(
                        invoice.getInvoiceNumber(),
                        invoice.getSalesOrderId(),
                        invoice.getCustomerId(),
                        invoice.getOrderDate(),
                        invoice.getTotalAmount(),
                        invoice.getPaidAmount(),
                        invoice.getBalanceAmount(),
                        invoice.getStatus()
                )
        );
    }

    public String exportMonthlySalesSummaryCsv() {
        List<String> rows = dashboardService.getMonthlySalesSummary().stream()
                .map(this::monthlySalesSummaryRow)
                .toList();
        return rowsWithHeader(row("month", "totalSalesAmount"), rows);
    }

    public String exportTopSellingProductsCsv() {
        List<String> rows = dashboardService.getTopSellingProducts().stream()
                .map(this::topSellingProductRow)
                .toList();
        return rowsWithHeader(row("itemId", "itemName", "totalQuantitySold"), rows);
    }

    public String exportSupplierPurchaseSummaryCsv() {
        List<String> rows = dashboardService.getSupplierPurchaseSummary().stream()
                .map(this::supplierPurchaseSummaryRow)
                .toList();
        return rowsWithHeader(row("supplierId", "supplierName", "totalPurchaseAmount"), rows);
    }

    private String monthlySalesSummaryRow(MonthlySalesSummaryDto summary) {
        return row(summary.getMonth(), summary.getTotalSalesAmount());
    }

    private String topSellingProductRow(TopSellingProductDto product) {
        return row(product.getItemId(), product.getItemName(), product.getTotalQuantitySold());
    }

    private String supplierPurchaseSummaryRow(SupplierPurchaseSummaryDto supplier) {
        return row(supplier.getSupplierId(), supplier.getSupplierName(), supplier.getTotalPurchaseAmount());
    }

    private String rowsWithHeader(String header, List<String> dataRows) {
        return Stream.concat(Stream.of(header), dataRows.stream())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String rows(String... rows) {
        return String.join(System.lineSeparator(), rows);
    }

    private String row(Object... values) {
        return Stream.of(values)
                .map(this::cell)
                .collect(Collectors.joining(","));
    }

    private String cell(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString();
        if (text.contains("\"") || text.contains(",") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
