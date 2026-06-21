package com.erp.manufacturing.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID", nullable = false)
    private Long notificationId;

    @Email(message = "Recipient email must be valid")
    @Size(max = 100, message = "Recipient email must not exceed 100 characters")
    @Column(name = "RECIPIENT_EMAIL", length = 100)
    private String recipientEmail;

    @NotBlank(message = "Subject is required")
    @Size(max = 150, message = "Subject must not exceed 150 characters")
    @Column(name = "SUBJECT", length = 150, nullable = false)
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    @Column(name = "MESSAGE", length = 1000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private NotificationStatus status;

    @Column(name = "SOURCE_TYPE", length = 50)
    private String sourceType;

    @Column(name = "SOURCE_ID")
    private Long sourceId;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "SENT_DATE")
    private LocalDateTime sentDate;
}
