package com.devlab.userservice.kafka.producer;

import com.devlab.userservice.kafka.event.UserCreatedEvent;
import com.devlab.userservice.kafka.event.UserDeletedEvent;
import com.devlab.userservice.kafka.topic.Topic;
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
