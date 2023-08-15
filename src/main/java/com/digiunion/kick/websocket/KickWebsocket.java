package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.pusher.client.connection.ConnectionState.DISCONNECTED;
@Log
public class KickWebsocket implements Closeable {

    private final KickClient client = new KickClient();
    private final Pusher pusher;

    private final List<com.pusher.client.channel.Channel> channelList = new ArrayList<com.pusher.client.channel.Channel>();
    private final static String viteRecapchaSiteKey = "6LfW60MjAAAAAKJlV_IW6cYl63zpKNuI4EMkxR9b";
    private final static String vitePuserAppKey = "eb1d5f283081a78b932c";
    private final static String vitePusherAppCluster = "us2";
    private final static String baseUrl = "https://dbxmjjzl5pc1g.cloudfront.net/c28817ac-d0f8-4f25-9b16-7c1565e46720/build/";
    public KickWebsocket() {
        pusher = new Pusher(vitePuserAppKey, new PusherOptions()
            .setHost("stats.pusher.com")
            .setCluster(vitePusherAppCluster)
            .setChannelAuthorizer(client));
    }
    public CompletableFuture<Void> connect(@NonNull Channel channel) {
        if (pusher.getConnection().getState() == DISCONNECTED)
            return CompletableFuture.runAsync(() -> {
                pusher.connect();
                log.info("connection established with " + pusher.getConnection().getSocketId());
            }).thenRun(subscribe(channel));
        return CompletableFuture.runAsync(subscribe(channel));
    }

    private Runnable subscribe(@NonNull Channel channel) {
        if (!pusher.subscribePresence("chatroom." + channel.chatroom().id()).isSubscribed()) {
            channelList.add(pusher.subscribe(String.valueOf(channel.chatroom().id())));
            log.info("subscribed to %s with %s".formatted(channel.slug(), channel.chatroom().id()));
        }
        return null;
    }

    @Override
    public void close() {
        pusher.disconnect();
    }
}
