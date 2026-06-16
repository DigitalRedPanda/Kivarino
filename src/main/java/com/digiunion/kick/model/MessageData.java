package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageData(
    Message data,
    String message
) {
public record Message(
    @JsonProperty("is_sent") boolean isSent,
    @JsonProperty("message_id") String messageId
) {}

}
