package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAssignmentRequest {
    private Long assignmentId;
    private Long employeeId;
    private @Size(max = 50) String role;
    private @PositiveOrZero BigDecimal hoursWorked;
    private LocalDateTime assignmentDate;
}
