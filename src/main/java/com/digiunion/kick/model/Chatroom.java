package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Chatroom {
    @JsonProperty
    private final int id;
    @JsonProperty("chatable_id")
    private final int chatableId;

    public Chatroom(@JsonProperty int id, @JsonProperty("chatable_id") int chatableId) {
        this.id = id;
        this.chatableId = chatableId;
    }

    @JsonProperty
    public int id() {
        return id;
    }

    @JsonProperty("chatable_id")
    public int chatableId() {
        return chatableId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Chatroom) obj;
        return this.id == that.id &&
                this.chatableId == that.chatableId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatableId);
    }

    @Override
    public String toString() {
        return "Chatroom[" +
                "id=" + id + ", " +
                "chatableId=" + chatableId + ']';
    }

}
