package com.digiunion.kick.model;

public record Account(long id, String name) {
    public Account {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        
    }
}
