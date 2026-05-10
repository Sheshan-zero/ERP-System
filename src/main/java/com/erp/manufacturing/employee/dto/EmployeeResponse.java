package com.erp.manufacturing.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long employeeId,
        String employeeName,
        String email,
        String contactNo,
        LocalDateTime hireDate,
        BigDecimal salary,
        String employeeType
) {
}
