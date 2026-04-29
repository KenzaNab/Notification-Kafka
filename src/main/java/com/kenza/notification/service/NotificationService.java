package com.kenza.notification.service;

import com.kenza.notification.dto.NotificationDto;
import com.kenza.notification.model.Notification;
import com.kenza.notification.producer.NotificationProducer;
import com.kenza.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class NotificationService {

    private final NotificationProducer producer;
    private final NotificationRepository repository;

    public NotificationDto.NotificationResponse send(NotificationDto.SendRequest req) {
        NotificationDto.NotificationEvent event = NotificationDto.NotificationEvent.builder()
                .id(UUID.randomUUID().toString()).type(req.getType())
                .recipient(req.getRecipient()).subject(req.getSubject())
                .message(req.getMessage()).build();
        producer.send(event);
        Notification saved = repository.save(Notification.builder()
                .id(event.getId()).type(req.getType()).recipient(req.getRecipient())
                .subject(req.getSubject()).message(req.getMessage()).build());
        return toResponse(saved);
    }

    public List<NotificationDto.NotificationResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<NotificationDto.NotificationResponse> getByRecipient(String recipient) {
        return repository.findByRecipient(recipient).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<NotificationDto.NotificationResponse> getByStatus(String status) {
        return repository.findByStatus(Notification.NotificationStatus.valueOf(status.toUpperCase()))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NotificationDto.NotificationResponse toResponse(Notification n) {
        return NotificationDto.NotificationResponse.builder()
                .id(n.getId()).type(n.getType()).recipient(n.getRecipient())
                .subject(n.getSubject()).status(n.getStatus())
                .retryCount(n.getRetryCount()).sentAt(n.getSentAt())
                .createdAt(n.getCreatedAt()).build();
    }
}
