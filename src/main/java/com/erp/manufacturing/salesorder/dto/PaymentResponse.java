package com.erp.manufacturing.salesorder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        LocalDateTime paymentDate,
        BigDecimal amount,
        String paymentMethod,
        String paymentStatus
) {
}
