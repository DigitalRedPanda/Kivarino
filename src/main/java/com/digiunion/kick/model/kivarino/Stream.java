package com.digiunion.kick.model.kivarino;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Stream(
    @JsonProperty("is_live") boolean isLive,
    @JsonProperty("is_mature") boolean isMature,
    @JsonProperty("key") String key,
    @JsonProperty("language") String language,
    @JsonProperty("start_time") String startTime,
    @JsonProperty("thumbnail") String thumbnail,
    @JsonProperty("url") String url,
    @JsonProperty("viewer_count") int viewerCount
) {}
