package com.digiunion.kick.websocket;

import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;

@ClientEndpoint
@Log
@RequiredArgsConstructor
public class KickClientWebsocket {
    @Getter
    private Session session;
    @NonNull
    @Getter
    private final ArrayList<Channel> channels;

    private final KickClient client = new KickClient();
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        val channelSlug = "digital-red-panda";
        log.info("connection has been established with socket id: " + session.getId());
        client.getChannel(channelSlug).thenCompose(channel -> client.requestToken("chatroom." + channel.chatroom().id(), session.getId())).join();
        try {
            session.getBasicRemote().sendText("""
                "pusher"
                """);
        } catch (IOException e) {

        }

    }
}
