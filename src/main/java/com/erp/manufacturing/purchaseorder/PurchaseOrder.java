package com.erp.manufacturing.purchaseorder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "PURCHASEORDER")
@Schema(description = "Purchase order with nested purchase order items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PURCHASE_ORDER_ID", nullable = false)
    private Long purchaseOrderId;

    @Column(name = "SUPPLIER_ID")
    private Long supplierId;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @Column(name = "EXPECTED_DATE")
    private LocalDateTime expectedDate;

    @Size(max = 30, message = "Status must not exceed 30 characters")
    @Column(name = "STATUS", length = 30)
    private String status;

    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Valid
    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
}
