package com.devlab.userservice.kafka.event;

public record UserCreatedEvent(Long userId, String email) {
}
