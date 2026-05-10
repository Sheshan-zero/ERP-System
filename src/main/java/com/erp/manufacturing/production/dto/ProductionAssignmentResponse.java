package com.erp.manufacturing.production.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductionAssignmentResponse(
        Long assignmentId,
        Long employeeId,
        String role,
        BigDecimal hoursWorked,
        LocalDateTime assignmentDate
) {
}
