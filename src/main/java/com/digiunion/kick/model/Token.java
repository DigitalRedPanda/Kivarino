package com.digiunion.kick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Token(String nameFieldName, String encryptedValidFrom) {
}
