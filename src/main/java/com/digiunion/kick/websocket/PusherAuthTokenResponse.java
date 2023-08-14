package com.digiunion.kick.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PusherAuthTokenResponse(String auth) {
}
