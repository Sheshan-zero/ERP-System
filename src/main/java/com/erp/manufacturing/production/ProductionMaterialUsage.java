package com.erp.manufacturing.production;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTIONMATERIALUSAGE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterialUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USAGE_ID", nullable = false)
    private Long usageId;

    @JsonBackReference(value = "production-material-usages")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTION_ORDER_ID")
    private ProductionOrder productionOrder;

    @Column(name = "RAW_MATERIAL_ID")
    private Long rawMaterialId;

    @DecimalMin(value = "0.00", message = "Quantity used cannot be negative")
    @Column(name = "QUANTITY_USED", precision = 10, scale = 2)
    private BigDecimal quantityUsed;

    @Column(name = "USAGE_DATE")
    private LocalDateTime usageDate;
}
