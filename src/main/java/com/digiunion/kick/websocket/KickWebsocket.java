package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.pusher.client.connection.ConnectionState.DISCONNECTED;

public class KickWebsocket implements Closeable {

    private final KickClient client = new KickClient();
    public final Pusher pusher;
    public final ArrayList<Channel> subscribedChannels = new ArrayList<>();
    public final static String viteRecapchaSiteKey = "6LfW60MjAAAAAKJlV_IW6cYl63zpKNuI4EMkxR9b";
    public final static String vitePuserAppKey = "eb1d5f283081a78b932c";
    public final static String vitePusherAppCluster = "us2";
    public final static String baseUrl = "https://dbxmjjzl5pc1g.cloudfront.net/c28817ac-d0f8-4f25-9b16-7c1565e46720/build/";

    public KickWebsocket(){
        pusher = new Pusher(vitePuserAppKey, new PusherOptions()
            .setHost("stats.pusher.com")
            .setCluster(vitePusherAppCluster)
        );
        pusher.connect();
    }

    public CompletableFuture<com.pusher.client.channel.Channel> connect(Channel channel){
        return CompletableFuture.supplyAsync(()-> {
            if(pusher.getConnection().getState() == DISCONNECTED)
                pusher.connect();
            final String channelName = "chatroom." + channel.chatroom().id();
            final com.pusher.client.channel.Channel pushChannel = pusher.subscribe(channelName);
//            val presenceChannel = Optional.ofNullable(pusher.getPresenceChannel("presence-chatroom"));
            System.out.println(pusher.subscribe(channelName));
            subscribedChannels.add(channel);
            System.out.printf("[\033[34mINFO\033[0m] subscribed to %s with chatroom %s\n", channel.slug(), channelName);
            return pushChannel;
        }, client.getExecutor());
    }
    @Override
    public void close() {
        pusher.disconnect();
    }
}
