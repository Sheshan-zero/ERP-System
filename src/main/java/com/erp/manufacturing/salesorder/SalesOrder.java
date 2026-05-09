package com.erp.manufacturing.salesorder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.erp.manufacturing.common.enums.SalesOrderStatus;
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
@Table(name = "SALESORDER")
@Schema(description = "Sales order with nested sales order items and payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALES_ORDER_ID", nullable = false)
    private Long salesOrderId;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @Size(max = 30, message = "Order status must not exceed 30 characters")
    @Column(name = "ORDER_STATUS", length = 30)
    @Enumerated(EnumType.STRING)
    private SalesOrderStatus orderStatus;

    @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Valid
    @Builder.Default
    @JsonManagedReference(value = "sales-order-items")
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> salesOrderItems = new ArrayList<>();

    @Valid
    @Builder.Default
    @JsonManagedReference(value = "sales-order-payments")
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
}
