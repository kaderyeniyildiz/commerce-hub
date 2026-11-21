package com.commercehub.user.kafka.event;

public record UserCreatedEvent(Long userId, String email) {
}
