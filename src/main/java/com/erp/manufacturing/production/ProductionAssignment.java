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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTIONASSIGNMENT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGNMENT_ID", nullable = false)
    private Long assignmentId;

    @JsonBackReference(value = "production-assignments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTION_ORDER_ID")
    private ProductionOrder productionOrder;

    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    @Column(name = "ROLE", length = 50)
    private String role;

    @DecimalMin(value = "0.00", message = "Hours worked cannot be negative")
    @Column(name = "HOURS_WORKED", precision = 6, scale = 2)
    private BigDecimal hoursWorked;

    @Column(name = "ASSIGNMENT_DATE")
    private LocalDateTime assignmentDate;
}
