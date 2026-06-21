package com.erp.manufacturing.customer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    private @NotBlank @Size(max = 100) String customerName;
    private @Size(max = 20) String contactNo;
    private @Email @Size(max = 100) String email;
    private @Size(max = 255) String address;
    private @Size(max = 30) String customerType;
    private LocalDateTime registrationDate;
}
