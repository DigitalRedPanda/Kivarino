package com.digiunion.kick;

import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Livestream;
import com.digiunion.kick.websocket.model.PusherAuthTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.client.ChannelAuthorizer;
import io.activej.eventloop.Eventloop;
import io.activej.http.AsyncHttpClient;
import io.activej.http.HttpRequest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.digiunion.kick.util.KickEndpoints.BASE_URL;
import static com.digiunion.kick.util.KickEndpoints.CHANNELS;
import static io.activej.http.HttpHeaders.ACCEPT_ENCODING;
import static io.activej.http.HttpHeaders.USER_AGENT;

/**
 * A kick api wrapper client that wraps kick endpoints into callable methods with OkHttpClient.
 */
public class KickClient implements ChannelAuthorizer {
    private final ObjectMapper mapper;
    private final Eventloop eventloop;
    private final AsyncHttpClient aCleint;
    // To be reused in different classes across the app
    private final ExecutorService executor;
    public ExecutorService getExecutor() {
      return executor;
    }
    private final static ThreadLocal <OkHttpClient> client = new ThreadLocal<>();
    private final static OkHttpClient rClient = new OkHttpClient.Builder().build();
    public OkHttpClient getRClient() {
      return rClient;
    }

    public KickClient(){
        Eventloop eventloop1;
        AsyncHttpClient aCleint1;
        ObjectMapper objectMapper1;
        ExecutorService executor1;
        try {
            eventloop1 = Eventloop.create();
            aCleint1 = AsyncHttpClient.create(eventloop1).withSslEnabled(SSLContext.getDefault(),eventloop1);
            objectMapper1 = new ObjectMapper();
            executor1 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() >> 1);
        } catch (NoSuchAlgorithmException e) {
            System.err.printf("[\033[34mSEVERE\033[0m] could not instantiate client; %s\n", e.getMessage());
            System.exit(1);
            eventloop1 = null;
            aCleint1 = null;
            objectMapper1 = null;
            executor1 = null;
        }
        eventloop = eventloop1;
        aCleint = aCleint1;
        mapper = objectMapper1;
        executor = executor1;
    }

    /**
     * Grabs a response asynchronously from KickEndpoints.CHANNELS.url using a clone instance of OkHttpClient and parses the json response then maps it to a Channel record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return CompletableFuture
     */
//    public CompletableFuture<Channel> getChannel(@NonNull String slug) {
//        return CompletableFuture.supplyAsync(() -> {
//            client.set(rClient);
//            try (val response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
//                assert response.body() != null;
//                return response.body().string();
//            } catch (IOException e) {
//                log.severe("could not execute %s's client call; %s".formatted(slug, e.getMessage()));
//                return null;
//            }
//        }, executor).thenApply(json -> {
//            try {
//                return mapper.readValue(json, Channel.class);
//            } catch (JsonProcessingException e) {
//                log.severe("could not process json for %s's channel; %s".formatted( slug, e));
//                return null;
//            }
//        });
//    }

    public CompletableFuture<Channel> getChannel(String slug){
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (final Response response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not execute %s's livestream client call; %s\n", slug, e.getMessage());
                return null;
            }
        }, executor).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class);
            } catch (JsonProcessingException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not process json for %s's livestream; %s\n",slug, e.getMessage());
                return null;
            }
        });
    }
    public CompletableFuture<Livestream> getLivestreamJ(String slug){
        final CompletableFuture<Livestream> resultt = eventloop.submit(()->
            aCleint.request(HttpRequest.get(CHANNELS.url.concat(slug))
                    .withHeader(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0")
                    .withHeader(ACCEPT_ENCODING,""))
                .then(lPlusRatio -> lPlusRatio.loadBody())
                .map(body -> body.getString(StandardCharsets.UTF_8))
                .whenComplete((result, exceptione) -> System.out.printf("[\033[34mINFO\033[0m] %s's livestream has been fetched\n", slug))
                .whenException(exception -> System.err.printf("[\033[31mSEVERE\033[0m] could not fetch %s's livestream; %s\n", slug, exception.getMessage()))
        ).thenApply(result -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return mapper.readValue(result, Channel.class).livestream();
            } catch (JsonProcessingException | InterruptedException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not parse %s's livestream; %s\n", slug, e.getMessage());
                return null;
            }
        });
        eventloop.run();
        return resultt;
    }
    public List<Channel> getAllChannels(List<String> channels){
        ArrayList<CompletableFuture<Channel>> futures = new ArrayList<>(channels.size());
        for (String channel : channels) {
            futures.add(getChannelEventloop(channel));
        }
        eventloop.run();
        return futures.stream().map(CompletableFuture::join).toList();
    }

    private CompletableFuture<Channel> getChannelEventloop(String slug){
        return eventloop.submit(()->
            aCleint.request(HttpRequest.get(CHANNELS.url.concat(slug))
                    .withHeader(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0")
                    .withHeader(ACCEPT_ENCODING,""))
                .then(lPlusRatio -> lPlusRatio.loadBody())
                .map(body -> body.getString(StandardCharsets.UTF_8))
//                .whenComplete((result, exceptione) -> log.info("%s's livestream has been fetched".formatted(slug)))
//                .whenException(exception -> log.severe("could not fetch %s's livestream; %s".formatted(slug, exception.getMessage())))
        ).thenApply(result -> {
            try {
                return mapper.readValue(result, Channel.class);
            } catch (JsonProcessingException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not parse %s; %s\n", slug, e.getMessage());
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
        try(final Response response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return mapper.readValue(response.body().string(), Channel.class);
        } catch (IOException e) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not send fetch %s's; %s\n", slug, e.getMessage());
            return null;
        }

    }

    public String getChannelJsonSync(String slug){
        try(final Response response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not send fetch %s's; %s", slug, e.getMessage());
            return null;
        }
    }


    public CompletableFuture<String> getChannelJson(String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (final Response response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not execute %s's client call; %s", slug, e.getMessage());
                return null;
            }
        }, executor);
    }

    /**
     * Grabs a response asynchronously from KickEndpoints.CHANNELS.url using a clone instance of OkHttpClient and parses the json response then maps it to a Livestream record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return CompletableFuture
     */
    public CompletableFuture<Livestream> getLivestream(String slug) {
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            try (final Response response = client.get().newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()) {
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not execute %s's livestream client call; %s", slug, e.getMessage());
                return null;
            }
        }, executor).thenApply(json -> {
            try {
                return mapper.readValue(json, Channel.class).livestream();
            } catch (JsonProcessingException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not process json for %s's livestream; %s\n", slug, e.getMessage());
                return null;
            }
        });
    }

    /**
     * Grabs a response synchronously from KickEndpoints.CHANNELS.url using OkHttpClient and parses the json response then maps it to a Livestream record
     * @param slug streamer username with minimized set of symbols and lowercase letters; e.g. username: United_States_Of_Qassim, slug: united-states-of-qassim
     * @return Livestream record instance
     */
    public Livestream getLivestreamSync(String slug) throws IOException {
        try(final Response response = rClient.newCall(new Request.Builder().url(CHANNELS.url.concat(slug)).get().build()).execute()){
            assert response.body() != null;
            return mapper.readValue(response.body().string(), Channel.class).livestream();
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
            System.err.printf("[\033[31mSEVERE\033[0m] could not execute requestToken; %s\n", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<String> requestToken(String chatroomId, String socketId){
        return CompletableFuture.supplyAsync(() -> {
            client.set(rClient);
            StringBuilder builder;
            System.out.println(builder = new StringBuilder().append("{\n\"socket_id\": \"").append(socketId).append("\",\n\"channel_name\": \"").append(chatroomId).append("\"\n}"));
            try(final Response response = client.get().newCall(new Request.Builder()
                .url(new URL(BASE_URL.url.concat("broadcasting/auth")))
                .post(RequestBody.create(builder.toString(), MediaType.parse("application/json"))).build()).execute()){
                assert response.body() != null && response.code() == 200;
                return response.body().string();
            } catch (IOException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not send token request; %s\n", e);
                return "";
            }}, executor).thenApply(json -> {
            try {
                System.out.println(json);
                return mapper.readValue(json, PusherAuthTokenResponse.class).auth();
            } catch (JsonProcessingException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not process json token; %s\n", e.getMessage());
                return "";
            }
        });

    }
}
