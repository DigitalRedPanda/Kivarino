package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import lombok.extern.java.Log;
import lombok.val;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.pusher.client.connection.ConnectionState.DISCONNECTED;

@Log
public class KickWebsocket implements Closeable {

    private final KickClient client = new KickClient();
    private final Pusher pusher;
    private final ArrayList<Channel> subscribedChannels = new ArrayList<>();
    private final static String viteRecapchaSiteKey = "6LfW60MjAAAAAKJlV_IW6cYl63zpKNuI4EMkxR9b";
    private final static String vitePuserAppKey = "eb1d5f283081a78b932c";
    private final static String vitePusherAppCluster = "us2";
    private final static String baseUrl = "https://dbxmjjzl5pc1g.cloudfront.net/c28817ac-d0f8-4f25-9b16-7c1565e46720/build/";

    public KickWebsocket(){
        pusher = new Pusher(vitePuserAppKey, new PusherOptions()
            .setHost("stats.pusher.com")
            .setCluster(vitePusherAppCluster)
            .setChannelAuthorizer(client)
        );
        pusher.connect();
    }

    public CompletableFuture<Void> connect(Channel channel){
        return CompletableFuture.supplyAsync(()-> {
            if(pusher.getConnection().getState() == DISCONNECTED)
                pusher.connect();
            val channelName = new StringBuilder("chatroom.").append(channel.chatroom().id()).toString();
            if(!pusher.getPresenceChannel(channelName).isSubscribed()) {
                pusher.subscribe(channelName);
                subscribedChannels.add(channel);
            }
            return null;
            }, client.getExecutor());
    }
    @Override
    public void close() {
        pusher.disconnect();
    }
}
