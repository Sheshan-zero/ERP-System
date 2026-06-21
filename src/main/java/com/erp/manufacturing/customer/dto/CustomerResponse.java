package com.erp.manufacturing.customer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private String customerName;
    private String contactNo;
    private String email;
    private String address;
    private String customerType;
    private LocalDateTime registrationDate;
}
