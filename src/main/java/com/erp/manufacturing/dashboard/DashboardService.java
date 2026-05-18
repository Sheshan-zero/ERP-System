package com.erp.manufacturing.dashboard;

import com.erp.manufacturing.dashboard.dto.MonthlySalesSummaryDto;
import com.erp.manufacturing.dashboard.dto.ProductionSummaryDto;
import com.erp.manufacturing.dashboard.dto.SupplierPurchaseSummaryDto;
import com.erp.manufacturing.dashboard.dto.TopSellingProductDto;
import com.erp.manufacturing.item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EntityManager entityManager;

    public List<Item> getLowStockItems() {
        return entityManager.createQuery("""
                        SELECT item
                        FROM Item item
                        WHERE item.currentStock <= item.reorderLevel
                        """, Item.class)
                .getResultList();
    }

    public List<TopSellingProductDto> getTopSellingProducts() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT i.item_id,
                               i.item_name,
                               COALESCE(SUM(soi.quantity), 0) AS total_quantity_sold
                        FROM salesorderitem soi
                        JOIN item i ON i.item_id = soi.finished_product_id
                        WHERE i.item_type = 'FinishedProduct'
                        GROUP BY i.item_id, i.item_name
                        ORDER BY total_quantity_sold DESC
                        """)
                .getResultList();

        return rows.stream()
                .map(row -> new TopSellingProductDto(
                        toLong(row[0]),
                        (String) row[1],
                        toBigDecimal(row[2])
                ))
                .toList();
    }

    public List<MonthlySalesSummaryDto> getMonthlySalesSummary() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT TO_CHAR(order_date, 'YYYY-MM') AS sales_month,
                               COALESCE(SUM(total_amount), 0) AS total_sales_amount
                        FROM salesorder
                        GROUP BY TO_CHAR(order_date, 'YYYY-MM')
                        ORDER BY sales_month
                        """)
                .getResultList();

        return rows.stream()
                .map(row -> new MonthlySalesSummaryDto(
                        (String) row[0],
                        toBigDecimal(row[1])
                ))
                .toList();
    }

    public ProductionSummaryDto getProductionSummary() {
        Object[] row = (Object[]) entityManager.createNativeQuery("""
                        SELECT COUNT(*) AS total_orders,
                               COALESCE(SUM(CASE WHEN LOWER(status) = 'completed' THEN 1 ELSE 0 END), 0) AS completed_orders,
                               COALESCE(SUM(CASE WHEN LOWER(status) = 'planned' THEN 1 ELSE 0 END), 0) AS planned_orders,
                               COALESCE(SUM(quantity_produced), 0) AS total_quantity_produced
                        FROM productionorder
                        """)
                .getSingleResult();

        return new ProductionSummaryDto(
                toLong(row[0]),
                toLong(row[1]),
                toLong(row[2]),
                toBigDecimal(row[3])
        );
    }

    public List<SupplierPurchaseSummaryDto> getSupplierPurchaseSummary() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT s.supplier_id,
                               s.supplier_name,
                               COALESCE(SUM(po.total_amount), 0) AS total_purchase_amount
                        FROM purchaseorder po
                        JOIN supplier s ON s.supplier_id = po.supplier_id
                        GROUP BY s.supplier_id, s.supplier_name
                        ORDER BY total_purchase_amount DESC
                        """)
                .getResultList();

        return rows.stream()
                .map(row -> new SupplierPurchaseSummaryDto(
                        toLong(row[0]),
                        (String) row[1],
                        toBigDecimal(row[2])
                ))
                .toList();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        return BigDecimal.valueOf(((Number) value).doubleValue());
    }
}
