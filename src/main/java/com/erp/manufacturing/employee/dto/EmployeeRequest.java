package com.erp.manufacturing.employee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeRequest(
        @NotBlank @Size(max = 100) String employeeName,
        @Email @Size(max = 100) String email,
        @Size(max = 20) String contactNo,
        LocalDateTime hireDate,
        @DecimalMin("0.00") BigDecimal salary,
        @Size(max = 30) String employeeType
) {
}
