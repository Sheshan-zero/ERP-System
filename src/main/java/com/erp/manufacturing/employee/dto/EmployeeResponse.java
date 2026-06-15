package com.erp.manufacturing.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long employeeId;
    private String employeeName;
    private String email;
    private String contactNo;
    private LocalDateTime hireDate;
    private BigDecimal salary;
    private String employeeType;
}