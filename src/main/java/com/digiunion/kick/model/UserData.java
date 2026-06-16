package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserData(
    Data[] data,
    String message
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
        String email,
        String name,
        @JsonProperty("profile_picture")
        String profilePicture,
        @JsonProperty("user_id")
        long userId 
    ) {}
}
