package com.erp.manufacturing.dashboard;

import com.erp.manufacturing.dashboard.dto.MonthlySalesSummaryDto;
import com.erp.manufacturing.dashboard.dto.ProductionSummaryDto;
import com.erp.manufacturing.dashboard.dto.SupplierPurchaseSummaryDto;
import com.erp.manufacturing.dashboard.dto.TopSellingProductDto;
import com.erp.manufacturing.common.DtoMapper;
import com.erp.manufacturing.item.dto.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Business intelligence and analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DtoMapper dtoMapper;

    @GetMapping("/low-stock-items")
    @Operation(summary = "Get low stock items", description = "Returns items where current stock is less than or equal to reorder level.")
    @ApiResponse(responseCode = "200", description = "Low stock items retrieved successfully")
    public ResponseEntity<List<ItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(dashboardService.getLowStockItems().stream()
                .map(item -> dtoMapper.map(item, ItemResponse.class))
                .toList());
    }

    @GetMapping("/reorder-alerts")
    @Operation(summary = "Get reorder alerts", description = "Returns items that have reached or fallen below reorder level.")
    @ApiResponse(responseCode = "200", description = "Reorder alerts retrieved successfully")
    public ResponseEntity<List<ItemResponse>> getReorderAlerts() {
        return ResponseEntity.ok(dashboardService.getLowStockItems().stream()
                .map(item -> dtoMapper.map(item, ItemResponse.class))
                .toList());
    }

    @GetMapping("/top-selling-products")
    @Operation(summary = "Get top selling products", description = "Returns finished products ordered by total quantity sold descending.")
    @ApiResponse(responseCode = "200", description = "Top selling products retrieved successfully")
    public ResponseEntity<List<TopSellingProductDto>> getTopSellingProducts() {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts());
    }

    @GetMapping("/monthly-sales-summary")
    @Operation(summary = "Get monthly sales summary", description = "Returns total sales amount grouped by month.")
    @ApiResponse(responseCode = "200", description = "Monthly sales summary retrieved successfully")
    public ResponseEntity<List<MonthlySalesSummaryDto>> getMonthlySalesSummary() {
        return ResponseEntity.ok(dashboardService.getMonthlySalesSummary());
    }

    @GetMapping("/production-summary")
    @Operation(summary = "Get production summary", description = "Returns production order counts and total quantity produced.")
    @ApiResponse(responseCode = "200", description = "Production summary retrieved successfully")
    public ResponseEntity<ProductionSummaryDto> getProductionSummary() {
        return ResponseEntity.ok(dashboardService.getProductionSummary());
    }

    @GetMapping("/supplier-purchase-summary")
    @Operation(summary = "Get supplier purchase summary", description = "Returns total purchase amount grouped by supplier.")
    @ApiResponse(responseCode = "200", description = "Supplier purchase summary retrieved successfully")
    public ResponseEntity<List<SupplierPurchaseSummaryDto>> getSupplierPurchaseSummary() {
        return ResponseEntity.ok(dashboardService.getSupplierPurchaseSummary());
    }
}
