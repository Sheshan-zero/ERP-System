package com.erp.manufacturing.notification;

import com.erp.manufacturing.notification.dto.NotificationRequest;
import com.erp.manufacturing.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request) {
        return Notification.builder()
                .recipientEmail(request.getRecipientEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getRecipientEmail(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getSourceType(),
                notification.getSourceId(),
                notification.getCreatedDate(),
                notification.getSentDate()
        );
    }

    public Page<NotificationResponse> toResponsePage(Page<Notification> page) {
        return page.map(this::toResponse);
    }
}
