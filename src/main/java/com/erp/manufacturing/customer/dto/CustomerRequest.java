package com.erp.manufacturing.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CustomerRequest(
        @NotBlank @Size(max = 100) String customerName,
        @Size(max = 20) String contactNo,
        @Email @Size(max = 100) String email,
        @Size(max = 255) String address,
        @Size(max = 30) String customerType,
        LocalDateTime registrationDate
) {
}
