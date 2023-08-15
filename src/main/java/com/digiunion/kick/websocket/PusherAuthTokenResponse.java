package com.digiunion.kick.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PusherAuthTokenResponse {
    private final String auth;

    public PusherAuthTokenResponse(String auth) {
        this.auth = auth;
    }

    public String auth() {
        return auth;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PusherAuthTokenResponse) obj;
        return Objects.equals(this.auth, that.auth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auth);
    }

    @Override
    public String toString() {
        return "PusherAuthTokenResponse[" +
                "auth=" + auth + ']';
    }

}
