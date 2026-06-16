package com.digiunion.kick.model.kivarino;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChannelApi(
    @JsonProperty("banner_picture") String bannerPicture,
    @JsonProperty("broadcaster_user_id") long broadcasterUserId,
    @JsonProperty("category") Category category,
    @JsonProperty("channel_description") String channelDescription,
    @JsonProperty("slug") String slug,
    @JsonProperty("stream") Stream stream,
    @JsonProperty("stream_title") String streamTitle
) {}
