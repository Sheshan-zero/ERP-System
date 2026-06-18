package com.erp.manufacturing.production;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.erp.manufacturing.common.enums.ProductionOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCTIONORDER")
@Schema(description = "Production order with nested material usage and employee assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCTION_ORDER_ID", nullable = false)
    private Long productionOrderId;

    @Column(name = "FINISHED_PRODUCT_ID")
    private Long finishedProductId;

    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    @Column(name = "PRODUCTION_DATE")
    private LocalDateTime productionDate;

    @DecimalMin(value = "0.00", message = "Quantity to produce cannot be negative")
    @Column(name = "QUANTITY_TO_PRODUCE", precision = 10, scale = 2)
    private BigDecimal quantityToProduce;

    @DecimalMin(value = "0.00", message = "Quantity produced cannot be negative")
    @Column(name = "QUANTITY_PRODUCED", precision = 10, scale = 2)
    private BigDecimal quantityProduced;

    @Column(name = "STATUS", length = 30)
    @Enumerated(EnumType.STRING)
    private ProductionOrderStatus status;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Size(max = 20, message = "Priority must not exceed 20 characters")
    @Column(name = "PRIORITY", length = 20)
    private String priority;

    @Valid
    @Builder.Default
    @JsonManagedReference(value = "production-material-usages")
    @OneToMany(mappedBy = "productionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionMaterialUsage> materialUsages = new ArrayList<>();

    @Valid
    @Builder.Default
    @JsonManagedReference(value = "production-assignments")
    @OneToMany(mappedBy = "productionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionAssignment> assignments = new ArrayList<>();

    @Version
    @Column(name = "VERSION")
    private Long version;
}
