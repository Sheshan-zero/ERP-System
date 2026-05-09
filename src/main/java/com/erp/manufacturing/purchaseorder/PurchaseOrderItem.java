package com.erp.manufacturing.purchaseorder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "PURCHASEORDERITEM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PURCHASE_ORDER_ITEM_ID", nullable = false)
    private Long purchaseOrderItemId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURCHASE_ORDER_ID")
    private PurchaseOrder purchaseOrder;

    @Column(name = "RAW_MATERIAL_ID")
    private Long rawMaterialId;

    @DecimalMin(value = "0.00", message = "Quantity cannot be negative")
    @Column(name = "QUANTITY", precision = 10, scale = 2)
    private BigDecimal quantity;

    @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
    @Column(name = "UNIT_PRICE", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00", message = "Line total cannot be negative")
    @Column(name = "LINE_TOTAL", precision = 12, scale = 2)
    private BigDecimal lineTotal;
}
