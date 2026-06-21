package com.erp.manufacturing.notification;

import com.erp.manufacturing.notification.dto.NotificationRequest;
import com.erp.manufacturing.notification.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Queue and inspect ERP notification records")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationMapper.toResponsePage(notificationService.getAllNotifications(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationMapper.toResponse(notificationService.getNotificationById(id)));
    }

    @PostMapping
    @Operation(summary = "Create queued notification")
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationMapper.toResponse(notificationService.createNotification(notification)));
    }

    @PostMapping("/{id}/sent")
    @Operation(summary = "Mark notification as sent")
    public ResponseEntity<NotificationResponse> markSent(@PathVariable Long id) {
        return ResponseEntity.ok(notificationMapper.toResponse(notificationService.markSent(id)));
    }
}
