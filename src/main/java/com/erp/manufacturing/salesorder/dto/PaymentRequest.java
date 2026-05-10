package com.erp.manufacturing.salesorder.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequest(
        LocalDateTime paymentDate,
        @Positive BigDecimal amount,
        @Size(max = 30) String paymentMethod,
        @Size(max = 30) String paymentStatus
) {
}
