package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Chatroom(@JsonProperty int id, @JsonProperty("chatable_id") int chatableId) {

    @Override
    public String toString() {
        return "Chatroom[" +
            "id=" + id + ", " +
            "chatableId=" + chatableId + ']';
    }

}
