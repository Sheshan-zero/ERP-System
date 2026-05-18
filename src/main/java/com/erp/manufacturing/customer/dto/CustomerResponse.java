package com.erp.manufacturing.customer.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long customerId,
        String customerName,
        String contactNo,
        String email,
        String address,
        String customerType,
        LocalDateTime registrationDate
) {
}
