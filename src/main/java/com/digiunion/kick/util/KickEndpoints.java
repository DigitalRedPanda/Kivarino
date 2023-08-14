package com.digiunion.kick.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KickEndpoints {

    BASE_URL("https://kick.com/"),
    API_V1(BASE_URL.url.concat("api/v1/")),
    API_V2(BASE_URL.url.concat("api/v2/")),
    CHANNELS(API_V2.url.concat("channels/")),
    USERS(API_V1.url.concat("users/"));

    public final String url;
}
