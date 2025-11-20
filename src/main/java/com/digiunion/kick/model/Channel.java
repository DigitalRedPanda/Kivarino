package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Channel(int id, String slug, Livestream livestream, User user, Chatroom chatroom) {
    public boolean isLive() {
        return livestream != null;
    }
}
