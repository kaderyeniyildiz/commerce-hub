package com.commercehub.user.config;

import com.commercehub.user.kafka.topic.Topic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private static final int PARTITIONS = 3;
    private static final int REPLICAS = 3;

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(Topic.USER_CREATED)
                           .partitions(PARTITIONS)
                           .replicas(REPLICAS)
                           .build();
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name(Topic.USER_DELETED)
                           .partitions(PARTITIONS)
                           .replicas(REPLICAS)
                           .build();
    }

/*    @Bean
    public NewTopic[] topics() {
        return new NewTopic[] {
                TopicBuilder.name("user_created")
                            .partitions(1)
                            .replicas(1)
                        .build(),

                TopicBuilder.name("user_deleted")
                            .partitions(1)
                            .replicas(1)
                        .build()
        };
    }*/
}
