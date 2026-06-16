package com.digiunion.kick.util;

public enum KickOfficialAPIEndpoints {
  BASE_URL("https://api.kick.com/public/v1/"),
  AUTH_URL("https://id.kick.com/"),
  CHANNELS(BASE_URL.url + "channels"),
  USERS(BASE_URL.url + "users"),
  OAUTH(AUTH_URL.url + "oauth/"),
  CHAT(BASE_URL.url + "chat"),
  TOKEN_INTROSPECTION(OAUTH.url + "token/introspect");
  
  public final String url;

  KickOfficialAPIEndpoints(String url) {
    this.url = url;
  }

}
