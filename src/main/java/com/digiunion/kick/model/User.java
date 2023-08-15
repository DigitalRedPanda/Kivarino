package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class User {
    @JsonProperty
    private final int id;
    @JsonProperty("username")
    private final String name;

    public User(@JsonProperty int id, @JsonProperty("username") String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty
    public int id() {
        return id;
    }

    @JsonProperty("username")
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "User[" +
                "id=" + id + ", " +
                "name=" + name + ']';
    }

}
