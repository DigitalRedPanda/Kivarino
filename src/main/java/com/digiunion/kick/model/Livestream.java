package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Livestream {
    @JsonProperty
    private final Thumbnail thumbnail;
    @JsonProperty("viewer_count")
    private final int viewerCount;

    public Livestream(@JsonProperty Thumbnail thumbnail, @JsonProperty("viewer_count") int viewerCount) {
        this.thumbnail = thumbnail;
        this.viewerCount = viewerCount;
    }

    @JsonProperty
    public Thumbnail thumbnail() {
        return thumbnail;
    }

    @JsonProperty("viewer_count")
    public int viewerCount() {
        return viewerCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Livestream) obj;
        return Objects.equals(this.thumbnail, that.thumbnail) &&
                this.viewerCount == that.viewerCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(thumbnail, viewerCount);
    }

    @Override
    public String toString() {
        return "Livestream[" +
                "thumbnail=" + thumbnail + ", " +
                "viewerCount=" + viewerCount + ']';
    }

}
