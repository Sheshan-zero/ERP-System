package com.erp.manufacturing.dashboard;

import com.erp.manufacturing.dashboard.dto.MonthlySalesSummaryDto;
import com.erp.manufacturing.dashboard.dto.ProductionSummaryDto;
import com.erp.manufacturing.dashboard.dto.SupplierPurchaseSummaryDto;
import com.erp.manufacturing.dashboard.dto.TopSellingProductDto;
import com.erp.manufacturing.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/low-stock-items")
    public ResponseEntity<List<Item>> getLowStockItems() {
        return ResponseEntity.ok(dashboardService.getLowStockItems());
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<TopSellingProductDto>> getTopSellingProducts() {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts());
    }

    @GetMapping("/monthly-sales-summary")
    public ResponseEntity<List<MonthlySalesSummaryDto>> getMonthlySalesSummary() {
        return ResponseEntity.ok(dashboardService.getMonthlySalesSummary());
    }

    @GetMapping("/production-summary")
    public ResponseEntity<ProductionSummaryDto> getProductionSummary() {
        return ResponseEntity.ok(dashboardService.getProductionSummary());
    }

    @GetMapping("/supplier-purchase-summary")
    public ResponseEntity<List<SupplierPurchaseSummaryDto>> getSupplierPurchaseSummary() {
        return ResponseEntity.ok(dashboardService.getSupplierPurchaseSummary());
    }
}
