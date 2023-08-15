package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Livestream(@JsonProperty Thumbnail thumbnail, @JsonProperty("viewer_count") int viewerCount) {
}
