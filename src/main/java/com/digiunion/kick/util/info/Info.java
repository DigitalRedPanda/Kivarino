package com.digiunion.kick.util.info;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

public class Info {
    private static Dotenv env = Dotenv.configure()
        .filename("kick.env")
        .load();
    @Getter
    private static final String token = env.get("TOKEN");
    @Getter
    private static final String username = env.get("USERNAME");
    @Getter
    private static final String password = env.get("PASSWORD");
}
