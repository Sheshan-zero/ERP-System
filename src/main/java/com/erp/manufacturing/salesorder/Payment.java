package com.erp.manufacturing.salesorder;

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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID", nullable = false)
    private Long paymentId;

    @JsonBackReference(value = "sales-order-payments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ORDER_ID")
    private SalesOrder salesOrder;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @DecimalMin(value = "0.00", message = "Amount cannot be negative")
    @Column(name = "AMOUNT", precision = 12, scale = 2)
    private BigDecimal amount;

    @Size(max = 30, message = "Payment method must not exceed 30 characters")
    @Column(name = "PAYMENT_METHOD", length = 30)
    private String paymentMethod;

    @Size(max = 30, message = "Payment status must not exceed 30 characters")
    @Column(name = "PAYMENT_STATUS", length = 30)
    private String paymentStatus;
}
