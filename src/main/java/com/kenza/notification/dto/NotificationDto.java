package com.kenza.notification.dto;

import com.kenza.notification.model.Notification;
import lombok.Data;
import java.time.LocalDateTime;

public class NotificationDto {
    @Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class NotificationEvent {
        private String id;
        private Notification.NotificationType type;
        private String recipient;
        private String subject;
        private String message;
    }

    @Data public static class SendRequest {
        private Notification.NotificationType type;
        private String recipient;
        private String subject;
        private String message;
    }

    @Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class NotificationResponse {
        private String id;
        private Notification.NotificationType type;
        private String recipient;
        private String subject;
        private Notification.NotificationStatus status;
        private int retryCount;
        private LocalDateTime sentAt;
        private LocalDateTime createdAt;
    }
}
