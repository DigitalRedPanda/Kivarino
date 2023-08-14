package com.digiunion.kick;

import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Livestream;
import com.digiunion.kick.websocket.PusherAuthTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.client.ChannelAuthorizer;
import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.digiunion.kick.util.KickEndpoints.BASE_URL;
import static com.digiunion.kick.util.KickEndpoints.CHANNELS;

/**
 * A kick api wrapper client that wraps kick endpoints into callable methods with HttpBrowserClient.
 */
@Log
public class KickClient implements ChannelAuthorizer {
    private final ObjectMapper mapper;
    private final static ThreadLocal <OkHttpClient> client = new ThreadLocal<>();
    private final static OkHttpClient rClient = new OkHttpClient.Builder().build();

    public KickClient(){
        mapper = new ObjectMapper();
    }
    public CompletableFuture<Channel> getChannel(@NonNull String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not execute %s's client call; %s".formatted(slug, e.getMessage()));
                return null;
            }
        }).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class);
            } catch (JsonProcessingException e) {
                log.severe("could not process json for %s's channel; %s".formatted(slug, e.getMessage()));
                return null;
            }
        });
    }

    public CompletableFuture<Livestream> getLivestream(@NonNull String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not execute %s's livestream client call; %s".formatted(slug, e.getMessage()));
                return null;
            }
        }).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class).livestream();
            } catch (JsonProcessingException e) {
                log.severe("could not process json for %s's livestream; %s".formatted(slug, e.getMessage()));
                return null;
            }
        });
    }



    @Override
    public String authorize(String chatroomId, String socketId) {
        try {
            return requestToken(chatroomId, socketId).get();
        } catch (InterruptedException | ExecutionException e) {
            log.severe("could not execute requestToken; " + e.getMessage());
            return null;
        }
    }

    public CompletableFuture<String> requestToken(String chatroomId, String socketId){
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try(val response = client.get().newCall(new Request.Builder()
                .url(new URL(BASE_URL.url.concat("broadcasting/auth")))
                .post(RequestBody.create("""
                   {
                   "socket_id": "%s",
                   "channel_name": "%s"
                   }
                    """.formatted(socketId, chatroomId), MediaType.parse("application/json"))).build()).execute()){
                return response.body().string();
        } catch (IOException e) {
                log.severe("could not send token request; " + e.getMessage());
                return "";
            }}).thenApply(json -> {
            try {
                return mapper.readValue(json, PusherAuthTokenResponse.class).auth();
            } catch (JsonProcessingException e) {
                log.severe("could not process json token; " + e.getMessage());
                return "";
            }
        });

    }
}

