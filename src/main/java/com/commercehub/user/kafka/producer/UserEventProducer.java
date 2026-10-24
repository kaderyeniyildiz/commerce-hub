package com.commercehub.user.kafka.producer;

import com.commercehub.user.kafka.event.UserCreatedEvent;
import com.commercehub.user.kafka.event.UserDeletedEvent;
import com.commercehub.user.kafka.topic.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public void sendUserCreatedEvent(Long userId, String email) {
        UserCreatedEvent event = new UserCreatedEvent(userId, email);
        String message = jsonMapper.writeValueAsString(event);
        kafkaTemplate.send(Topic.USER_CREATED, String.valueOf(userId), message);
    }

    public void sendUserDeletedEvent(Long userId) {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        String message = jsonMapper.writeValueAsString(event);
        kafkaTemplate.send(Topic.USER_DELETED, String.valueOf(userId), message);
    }
}
