package org.k.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicDto {
    private final String topic;

    @JsonCreator
    public TopicDto(@JsonProperty("topic") String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
