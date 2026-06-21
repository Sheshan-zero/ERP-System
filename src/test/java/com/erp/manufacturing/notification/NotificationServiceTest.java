package com.erp.manufacturing.notification;

import com.erp.manufacturing.common.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotificationAlwaysQueuesNewRecord() {
        Notification request = Notification.builder()
                .notificationId(99L)
                .recipientEmail("manager@example.com")
                .subject("Approval required")
                .message("Purchase order requires approval")
                .status(NotificationStatus.Failed)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification saved = notificationService.createNotification(request);

        assertThat(saved.getNotificationId()).isNull();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.Queued);
        assertThat(saved.getCreatedDate()).isNotNull();
    }

    @Test
    void queueSystemNotificationStoresSourceReference() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification saved = notificationService.queueSystemNotification(
                "customer@example.com",
                "Sales order delivered",
                "Sales order 42 was delivered.",
                "SALESORDER",
                42L
        );

        assertThat(saved.getRecipientEmail()).isEqualTo("customer@example.com");
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.Queued);
        assertThat(saved.getSourceType()).isEqualTo("SALESORDER");
        assertThat(saved.getSourceId()).isEqualTo(42L);
        assertThat(saved.getCreatedDate()).isNotNull();
    }

    @Test
    void markSentSetsStatusAndSentDate() {
        Notification existing = Notification.builder()
                .notificationId(10L)
                .subject("Queued")
                .message("Queued message")
                .status(NotificationStatus.Queued)
                .createdDate(LocalDateTime.now().minusMinutes(5))
                .build();
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(existing)).thenReturn(existing);

        Notification saved = notificationService.markSent(10L);

        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.Sent);
        assertThat(saved.getSentDate()).isNotNull();
    }

    @Test
    void markSentRejectsMissingNotification() {
        when(notificationRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markSent(77L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Notification not found");
    }
}
