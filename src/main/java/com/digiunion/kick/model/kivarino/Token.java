package com.digiunion.kick.model.kivarino;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Token(String refresh_token,
                          String scope,
                          String access_token,
                          long expiresIn
                          ) {

}


