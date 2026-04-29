package com.kenza.notification.controller;

import com.kenza.notification.dto.NotificationDto;
import com.kenza.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/notifications")
@RequiredArgsConstructor @Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping @Operation(summary = "Send a notification via Kafka")
    public ResponseEntity<NotificationDto.NotificationResponse> send(@RequestBody NotificationDto.SendRequest req) {
        return ResponseEntity.status(201).body(notificationService.send(req));
    }

    @GetMapping @Operation(summary = "List all notifications")
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @GetMapping("/recipient/{recipient}") @Operation(summary = "Notifications by recipient")
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getByRecipient(recipient));
    }

    @GetMapping("/status/{status}") @Operation(summary = "Notifications by status")
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(notificationService.getByStatus(status));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("Notification Service is running"); }
}
