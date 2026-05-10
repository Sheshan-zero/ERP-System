package com.erp.manufacturing.production.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductionAssignmentRequest(
        Long assignmentId,
        Long employeeId,
        @Size(max = 50) String role,
        @PositiveOrZero BigDecimal hoursWorked,
        LocalDateTime assignmentDate
) {
}
