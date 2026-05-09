package com.erp.manufacturing.billofmaterial;

import com.erp.manufacturing.item.Item;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "BILLOFMATERIAL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOM_ID", nullable = false)
    private Long bomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FINISHED_PRODUCT_ID")
    @NotNull(message = "Finished product is required")
    private Item finishedProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RAW_MATERIAL_ID")
    @NotNull(message = "Raw material is required")
    private Item rawMaterial;

    @NotNull(message = "Required quantity is required")
    @Positive(message = "Required quantity must be greater than 0")
    @Column(name = "REQUIRED_QUANTITY", precision = 10, scale = 2)
    private BigDecimal requiredQuantity;

    @DecimalMin(value = "0.00", message = "Wastage percentage cannot be negative")
    @Column(name = "WASTAGE_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal wastagePercentage;
}
