package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

@ClientEndpoint
@Log
@RequiredArgsConstructor
public class KickClientWebsocket {
    @Nullable
    public Session session;
    @NonNull
    public final ArrayList<Channel> channels;

    private final KickClient client = new KickClient();
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        val channelSlug = "digital-red-panda";
        log.info("connection has been established with socket id: " + session.getId());
        val channel = client.getChannel(channelSlug).join();
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
            log.severe("could not connect websocket; " + e.getMessage());
        }

    }

    @OnMessage
    public void onMessage(String message){
        System.out.println(message);
    }
}
