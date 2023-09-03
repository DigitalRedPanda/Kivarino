package com.digiunion.kick;

import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Livestream;
import com.digiunion.kick.websocket.model.PusherAuthTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.client.ChannelAuthorizer;
import lombok.Getter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.digiunion.kick.util.KickEndpoints.BASE_URL;
import static com.digiunion.kick.util.KickEndpoints.CHANNELS;

/**
 * A kick api wrapper client that wraps kick endpoints into callable methods with OkHttpClient.
 */
@Log
public class KickClient implements ChannelAuthorizer {
    private final ObjectMapper mapper;
    // To be reused in different classes across the app
    @Getter
    private final ExecutorService executor;
    private final static ThreadLocal <OkHttpClient> client = new ThreadLocal<>();
    @Getter
    private final static OkHttpClient rClient = new OkHttpClient.Builder().build();

    public KickClient(){
        mapper = new ObjectMapper();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() >> 1);
    }

    /**
     * Grabs a response asynchronously from KickEndpoints.CHANNELS.url using a clone instance of OkHttpClient and parses the json response then maps it to a Channel record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return CompletableFuture
     */
    public CompletableFuture<Channel> getChannel(@NonNull String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not execute %s's client call; %s".formatted(slug, e.getMessage()));
                return null;
            }
        }, executor).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class);
            } catch (JsonProcessingException e) {
                log.severe("could not process json for %s's channel; %s".formatted( slug, e));
                return null;
            }
        });
    }

    /**
     * Grabs a response synchronously from KickEndpoints.CHANNELS.url using OkHttpClient and parses the json response then maps it to a Channel record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return Channel
     */
    public Channel getChannelSync(String slug){
        try(val response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return mapper.readValue(response.body().string(), Channel.class);
        } catch (IOException e) {
            log.severe("could not send fetch %s's; %s".formatted(slug, e.getMessage()));
            return null;
        }

    }

    public String getChannelJsonSync(String slug){
        try(val response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            log.severe("could not send fetch %s's; %s".formatted(slug, e.getMessage()));
            return null;
        }
    }


    public CompletableFuture<String> getChannelJson(String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not execute %s's client call; %s".formatted(slug, e.getMessage()));
                return null;
            }
        }, executor);
    }

    /**
     * Grabs a response asynchronously from KickEndpoints.CHANNELS.url using a clone instance of OkHttpClient and parses the json response then maps it to a Livestream record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return CompletableFuture
     */
    public CompletableFuture<Livestream> getLivestream(@NonNull String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not execute %s's livestream client call; %s".formatted(slug, e.getMessage()));
                return null;
            }
        }, executor).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class).livestream();
            } catch (JsonProcessingException e) {
                log.severe("could not process json for %s's livestream; %s".formatted(slug, e.getMessage()));
                return null;
            }
        });
    }

    /**
     * Grabs a response synchronously from KickEndpoints.CHANNELS.url using OkHttpClient and parses the json response then maps it to a Livestream record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return Livestream record instance
     */
    public Livestream getLivestreamSync(String slug){
        try(val response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return mapper.readValue(response.body().string(), Channel.class).livestream();
        } catch (IOException e) {
            log.severe("could not send fetch %s's; %s".formatted(slug, e.getMessage()));
            return null;
        }
    }

    /**
     *
     * @param channelName
     * @param socketId
     * @return String Authorization token
     */
    @Override
    public String authorize(String channelName, String socketId) {
        try {
            return requestToken(channelName, socketId).get();
        } catch (InterruptedException | ExecutionException e) {
            log.severe("could not execute requestToken; " + e.getMessage());
            return null;
        }
    }

    public CompletableFuture<String> requestToken(String channelName, String socketId){
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            val builder = new StringBuilder().append("{\"socket_id\": \"").append(socketId).append("\",\"channel_name\": \"").append(channelName).append("\"}");
            try(val response = client.get().newCall(new Request.Builder()
                .url(new URL(BASE_URL.url.concat("broadcasting/auth")))
                .post(RequestBody.create(builder.toString(), MediaType.parse("application/json"))).build()).execute()){
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                log.severe("could not send token request; " + e.getMessage());
                return "";
            }}, executor).thenApply(json -> {
            try {
                return mapper.readValue(json, PusherAuthTokenResponse.class).auth();
            } catch (JsonProcessingException e) {
                log.severe("could not process json token; " + e.getMessage());
                return "";
            }
        });

    }
}
