package com.digiunion.kick.model.kivarino;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Credentials(long account_id,
                          String refresh_token,
                          String scope,
                          String access_token,
                          @JsonProperty(namespace = "exp")
                          long expiresIn,
                          Timestamp issued_date
                          ) {
  public boolean tokensEquals(Credentials cred2) {
    return this.access_token().equals(cred2.access_token()) && this.refresh_token().equals(cred2.refresh_token());
  }

}


