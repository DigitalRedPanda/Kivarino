package com.digiunion.kick.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PusherAuthTokenResponse(String auth) {
}
