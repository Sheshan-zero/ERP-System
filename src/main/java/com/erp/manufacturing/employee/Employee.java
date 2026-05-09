package com.erp.manufacturing.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @NotBlank(message = "Employee name is required")
    @Size(max = 100, message = "Employee name must not exceed 100 characters")
    @Column(name = "EMPLOYEE_NAME", length = 100)
    private String employeeName;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "EMAIL", length = 100)
    private String email;

    @Size(max = 20, message = "Contact number must not exceed 20 characters")
    @Column(name = "CONTACT_NO", length = 20)
    private String contactNo;

    @Column(name = "HIRE_DATE")
    private LocalDateTime hireDate;

    @DecimalMin(value = "0.00", message = "Salary cannot be negative")
    @Column(name = "SALARY", precision = 10, scale = 2)
    private BigDecimal salary;

    @Size(max = 30, message = "Employee type must not exceed 30 characters")
    @Column(name = "EMPLOYEE_TYPE", length = 30)
    private String employeeType;
}
