package com.erp.manufacturing.production.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAssignmentResponse {
    private Long assignmentId;
    private Long employeeId;
    private String role;
    private BigDecimal hoursWorked;
    private LocalDateTime assignmentDate;
}
