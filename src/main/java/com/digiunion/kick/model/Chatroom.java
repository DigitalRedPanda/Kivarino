package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Chatroom(int id, @JsonProperty("chatable_id") int chatableId) {
}
