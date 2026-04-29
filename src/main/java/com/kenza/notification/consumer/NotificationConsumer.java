package com.kenza.notification.consumer;

import com.kenza.notification.dto.NotificationDto;
import com.kenza.notification.model.Notification;
import com.kenza.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = ".dead-letter"
    )
    @KafkaListener(topics = "${notification.topics.email}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEmail(NotificationDto.NotificationEvent event) {
        log.info("Processing EMAIL notification for: {}", event.getRecipient());
        Notification notification = findOrCreate(event);
        try {
            sendEmail(event.getRecipient(), event.getSubject(), event.getMessage());
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to: {}", event.getRecipient());
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            log.error("Failed to send email to {}: {}", event.getRecipient(), e.getMessage());
            throw e;
        } finally {
            notificationRepository.save(notification);
        }
    }

    @KafkaListener(topics = "${notification.topics.push}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePush(NotificationDto.NotificationEvent event) {
        log.info("Processing PUSH notification for: {}", event.getRecipient());
        Notification notification = findOrCreate(event);
        // Simulated push notification
        log.info("Push notification sent to device: {}", event.getRecipient());
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @KafkaListener(topics = "${notification.topics.sms}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSms(NotificationDto.NotificationEvent event) {
        log.info("Processing SMS notification for: {}", event.getRecipient());
        Notification notification = findOrCreate(event);
        // Simulated SMS
        log.info("SMS sent to: {} — {}", event.getRecipient(), event.getMessage());
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @KafkaListener(topics = "notification.email.dead-letter", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDeadLetter(NotificationDto.NotificationEvent event) {
        log.error("DEAD LETTER: notification {} for {} permanently failed", event.getId(), event.getRecipient());
        notificationRepository.findById(event.getId()).ifPresent(n -> {
            n.setStatus(Notification.NotificationStatus.DEAD_LETTER);
            notificationRepository.save(n);
        });
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to); msg.setSubject(subject); msg.setText(body);
        mailSender.send(msg);
    }

    private Notification findOrCreate(NotificationDto.NotificationEvent event) {
        return notificationRepository.findById(event.getId()).orElseGet(() ->
            Notification.builder().id(event.getId()).type(event.getType())
                .recipient(event.getRecipient()).subject(event.getSubject())
                .message(event.getMessage()).build());
    }
}
