package com.erp.manufacturing.salesorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long paymentId;
    private LocalDateTime paymentDate;
    private @Positive BigDecimal amount;
    private @Size(max = 30) String paymentMethod;
    private @Size(max = 30) String paymentStatus;
}
