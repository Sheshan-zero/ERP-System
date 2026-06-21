package com.erp.manufacturing.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.erp.manufacturing.notification.NotificationStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private String recipientEmail;
    private String subject;
    private String message;
    private NotificationStatus status;
    private String sourceType;
    private Long sourceId;
    private LocalDateTime createdDate;
    private LocalDateTime sentDate;
}
