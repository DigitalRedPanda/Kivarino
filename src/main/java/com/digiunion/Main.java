package com.digiunion;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.digiunion.kick.util.KickEndpoints.CHANNELS;

/**
 * Main is the class where the project runs javafx and the main method
 */
@Log
public final class Main {

    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: final String[]
     */
    public static void main(String[] args) {
//        GUI.launch(args);
        val mapper = new ObjectMapper();
        val client = new KickClient();
        var time1 = System.currentTimeMillis();
        val channel1 = client.getChannel("copynine").thenAccept(System.out::println);
        val channel2 = client.getChannel("narash").thenAccept(System.out::println);
        val channel3 = client.getChannel("rustytheowl").thenAccept(System.out::println);
        val channel4 = client.getChannel("quillcannon").thenAccept(System.out::println);
        val channel5 = client.getChannel("krippyx").thenAccept(System.out::println);
        val channel6 = client.getChannel("luraxz").thenAccept(System.out::println);
        val channel7 = client.getChannel("brathaifa").thenAccept(System.out::println);
        val channel8 = client.getChannel("migren2009").thenAccept(System.out::println);
        CompletableFuture.allOf(channel1, channel2, channel3, channel4, channel5, channel6).join();
        var time2 = System.currentTimeMillis();
        System.out.printf("executed for %dms\n", time2 - time1);
        val clientS = new OkHttpClient.Builder().build();
        time1 = System.currentTimeMillis();
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("copynine")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string() ,Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("copynine", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("narash")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
//            System.out.println(response.body().string());
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("narash", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("quillcannon")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("quillcannon", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("rustytheowl")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("rustytheowl", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("krippyx")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("krippyx", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("luraxz")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("luraxz", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("brathaifa")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("brathaifa", e.getMessage()));
        }
        try(val response = clientS.newCall(new Request.Builder().url(CHANNELS.url.concat("migren2009")).get().build()).execute()) {
            val result = mapper.readValue(response.body().string(), Channel.class);
            System.out.println(result);
        } catch (IOException e) {
            log.severe("could not send client call for %s, %s".formatted("migren2009", e.getMessage()));
        }
        time2 = System.currentTimeMillis();
        System.out.printf("executed for %dms\n", time2 - time1);
    }
}

