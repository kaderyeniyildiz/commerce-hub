package com.devlab.userservice.kafka.consumer;

import com.devlab.userservice.kafka.event.UserCreatedEvent;
import com.devlab.userservice.kafka.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import static com.devlab.userservice.kafka.topic.Topic.USER_CREATED;
import static com.devlab.userservice.kafka.topic.Topic.USER_DELETED;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = USER_CREATED, groupId = "devlab-user-group")
    public void consumeUserCreatedEvent(String message) {
        UserCreatedEvent event = jsonMapper.readValue(message, UserCreatedEvent.class);
        log.info("Processing user created event: UserId: {}", event.userId());
    }

    @KafkaListener(topics = USER_DELETED, groupId = "devlab-user-group")
    public void consumeUserDeletedEvent(String message) {
        UserDeletedEvent event = jsonMapper.readValue(message, UserDeletedEvent.class);
        log.info("User deleted: Id: {}", event.userId());
    }
}
