package com.erp.manufacturing.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private @Email @Size(max = 100) String recipientEmail;
    private @NotBlank @Size(max = 150) String subject;
    private @NotBlank @Size(max = 1000) String message;
    private @Size(max = 50) String sourceType;
    private Long sourceId;
}
