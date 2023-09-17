package com.digiunion;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import lombok.ToString;

@ToString
public class Channel {
    @Serialize
    public final int id;
    @Serialize
    public final String slug;
    public Channel(@Deserialize("id") int id, @Deserialize("slug") String slug){
        this.id = id;
        this.slug = slug;
    }
}
