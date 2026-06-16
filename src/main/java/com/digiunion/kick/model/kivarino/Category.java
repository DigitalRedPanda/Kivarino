package com.digiunion.kick.model.kivarino;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Category(
    @JsonProperty("id") long id,
    @JsonProperty("name") String name,
    @JsonProperty("thumbnail") String thumbnail
) {}
