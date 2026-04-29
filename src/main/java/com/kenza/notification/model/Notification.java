package com.kenza.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private NotificationType type;

    @Column(nullable = false) private String recipient;
    @Column(nullable = false) private String subject;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String errorMessage;
    private int retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = NotificationStatus.PENDING;
    }

    public enum NotificationType { EMAIL, PUSH, SMS }
    public enum NotificationStatus { PENDING, SENT, FAILED, DEAD_LETTER }
}
