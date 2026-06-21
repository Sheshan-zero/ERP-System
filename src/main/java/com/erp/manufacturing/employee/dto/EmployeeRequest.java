package com.erp.manufacturing.employee.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    private @NotBlank @Size(max = 100) String employeeName;
    private @Email @Size(max = 100) String email;
    private @Size(max = 20) String contactNo;
    private LocalDateTime hireDate;
    private @DecimalMin("0.00") BigDecimal salary;
    private @Size(max = 30) String employeeType;
}
