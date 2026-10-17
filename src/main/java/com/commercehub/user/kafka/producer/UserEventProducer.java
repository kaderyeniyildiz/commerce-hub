package com.commercehub.user.kafka.producer;

import com.commercehub.user.kafka.event.UserCreatedEvent;
import com.commercehub.user.kafka.event.UserDeletedEvent;
import com.commercehub.user.kafka.topic.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserCreatedEvent(Long userId, String email) {
        UserCreatedEvent event = new UserCreatedEvent(userId, email);
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(Topic.USER_CREATED, String.valueOf(userId), message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize UserDeletedEvent", e);
        }
    }

    public void sendUserDeletedEvent(Long userId) {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(Topic.USER_DELETED, String.valueOf(userId), message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize UserDeletedEvent", e);
        }
    }

}
