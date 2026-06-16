package com.digiunion.kick.model.kivarino;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChannelData(
    @JsonProperty("data") ChannelApi[] data, @JsonProperty("message") String message) {}
