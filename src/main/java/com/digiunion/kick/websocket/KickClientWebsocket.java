package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

@ClientEndpoint
public class KickClientWebsocket {
    @Nullable
    public Session session;
    public final ArrayList<Channel> channels;

    private final KickClient client = new KickClient();
    public KickClientWebsocket(ArrayList<Channel> channels) {
      this.channels = channels;
    }
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        final String channelSlug = "digital-red-panda";
        System.out.printf("connection has been established with socket id: %s\n", session.getId());
        final Channel channel = client.getChannel(channelSlug).join();
//        val auth = CompletableFuture.supplyAsync(() -> client.requestToken("chatroom." + channel.chatroom().id(), session.getId())).join();
        try {
            session.getBasicRemote().sendText("""
                {
                "event": "pusher:subscribe",
                "data": {
                  "auth": "",
                  "channel": "channel.%s"
                  }
                }
                """.formatted(channel.id()));
            session.getBasicRemote().sendText("""
                {
                "event": "pusher:subscribe",
                 "data": {
                   "auth": "",
                   "channel": "chatrooms.%s"
                   }
                 }
                 """.formatted(channel.chatroom().id())
            );
        } catch (IOException e) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not connect websocket; %s\n", e.getMessage());
        }

    }

    @OnMessage
    public void onMessage(String message){
        System.out.println(message);
    }
}
