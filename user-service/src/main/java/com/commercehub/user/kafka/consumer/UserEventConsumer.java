package com.commercehub.user.kafka.consumer;

import com.commercehub.user.kafka.event.UserCreatedEvent;
import com.commercehub.user.kafka.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import static com.commercehub.user.kafka.topic.Topic.USER_CREATED;
import static com.commercehub.user.kafka.topic.Topic.USER_DELETED;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = USER_CREATED, groupId = "commercehub-user-group")
    public void consumeUserCreatedEvent(String message) {
        UserCreatedEvent event = jsonMapper.readValue(message, UserCreatedEvent.class);
        log.info("Processing user created event: UserId: {}", event.userId());
    }

    @KafkaListener(topics = USER_DELETED, groupId = "commercehub-user-group")
    public void consumeUserDeletedEvent(String message) {
        UserDeletedEvent event = jsonMapper.readValue(message, UserDeletedEvent.class);
        log.info("User deleted: Id: {}", event.userId());
    }
}
