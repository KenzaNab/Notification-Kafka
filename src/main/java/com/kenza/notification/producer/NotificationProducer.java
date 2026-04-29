package com.kenza.notification.producer;

import com.kenza.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationDto.NotificationEvent> kafkaTemplate;

    @Value("${notification.topics.email}") private String emailTopic;
    @Value("${notification.topics.push}") private String pushTopic;
    @Value("${notification.topics.sms}") private String smsTopic;

    public void send(NotificationDto.NotificationEvent event) {
        String topic = switch (event.getType()) {
            case EMAIL -> emailTopic;
            case PUSH -> pushTopic;
            case SMS -> smsTopic;
        };
        kafkaTemplate.send(topic, event.getId(), event);
        log.info("Sent {} notification to topic '{}' for recipient '{}'",
                event.getType(), topic, event.getRecipient());
    }
}
