package com.digiunion.kick.model;

public record TokenData(
        boolean active,
        String clientId,
        long expirationTime,
        String scope,
        String tokenType
    ) {}

