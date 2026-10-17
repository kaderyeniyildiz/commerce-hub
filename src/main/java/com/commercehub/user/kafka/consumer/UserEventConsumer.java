package com.commercehub.user.kafka.consumer;

import com.commercehub.user.kafka.event.UserCreatedEvent;
import com.commercehub.user.kafka.event.UserDeletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.commercehub.user.kafka.topic.Topic.USER_CREATED;
import static com.commercehub.user.kafka.topic.Topic.USER_DELETED;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = USER_CREATED, groupId = "commercehub-user-group")
    public void consumeUserCreatedEvent(String message) {
        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            log.info("Processing user created event: UserId: {}", event.userId());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid UserCreatedEvent JSON", e);
        }
    }

    @KafkaListener(topics = USER_DELETED, groupId = "commercehub-user-group")
    public void consumeUserDeletedEvent(String message) {
        try {
            UserDeletedEvent event = objectMapper.readValue(message, UserDeletedEvent.class);
            log.info("User deleted: Id: {}", event.userId());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid UserDeletedEvent JSON", e);
        }
    }
}
