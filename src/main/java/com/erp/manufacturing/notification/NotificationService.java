package com.erp.manufacturing.notification;

import com.erp.manufacturing.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    public Notification createNotification(Notification notification) {
        notification.setNotificationId(null);
        notification.setStatus(NotificationStatus.Queued);
        notification.setCreatedDate(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Notification queueSystemNotification(
            String recipientEmail,
            String subject,
            String message,
            String sourceType,
            Long sourceId
    ) {
        return notificationRepository.save(Notification.builder()
                .recipientEmail(recipientEmail)
                .subject(subject)
                .message(message)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .status(NotificationStatus.Queued)
                .createdDate(LocalDateTime.now())
                .build());
    }

    public Notification markSent(Long id) {
        Notification notification = getNotificationById(id);
        notification.setStatus(NotificationStatus.Sent);
        notification.setSentDate(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
}
