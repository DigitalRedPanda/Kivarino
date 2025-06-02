package com.digiunion.kick.util.info;
import io.github.cdimascio.dotenv.Dotenv;

public class Info {
    private static Dotenv env = Dotenv.configure()
        .filename("kick.env")
        .load();
    private static final String token = env.get("TOKEN");
    private static final String username = env.get("USERNAME");
    private static final String password = env.get("PASSWORD");

    public String getPassword() {
      return password;
    }
    public String getUsername() {
      return username;
    }
    public String getToken() {
      return token;
    }
    
}
