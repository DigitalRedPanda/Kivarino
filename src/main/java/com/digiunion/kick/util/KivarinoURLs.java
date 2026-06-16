package com.digiunion.kick.util;

public enum KivarinoURLs {
  BASE_URL("https://kivarino.xyz/"),
  WEBSOCKET("wss://kivarino.xyz/"),
  OAUTH(BASE_URL.url + "oauth/token"),
  EVENTS(WEBSOCKET.url + "events");

  public final String url;

  KivarinoURLs(String url) {
    this.url = url;
  }
}
