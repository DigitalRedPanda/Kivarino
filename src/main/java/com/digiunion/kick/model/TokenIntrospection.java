package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenIntrospection(
    TokenData data,
    String message
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TokenData(
        boolean active,
        String clientId,
        long expirationTime,
        String scope,
        String tokenType
    ) {}
}
