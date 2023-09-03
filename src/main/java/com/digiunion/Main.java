package com.digiunion;

import com.digiunion.kick.KickClient;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * Main is the class where the project runs javafx within the main method
 */
@Log
public final class Main {
    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(@Nullable String[] args) throws InterruptedException, ExecutionException, IOException, NoSuchAlgorithmException {
//
//        val database = Database.getInstance();
//        database.dropChannels();
        val client = new KickClient();
//        val channels = new String[]{
//            "dote","narash","quillcannon","rowex","krippyx","amzor10","rustytheowl","migren2009", "xqc", "adinross", "amzor10","0badbad0", "steeila","moerawn","luraxz","umko","isaudd","phantmx","knightx","copynine","playaway","psiko","amouranth","trainwreckstv","abumashal"
//        };
//        val futures = Arrays.stream(channels).map(client::getChannel).toList();
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
//        val futuress = futures.stream().map(CompletableFuture::join).toList();
//        Livestream livestream;
//        User user;
//
//        log.info("executing stringbuilder...");
//        var performance1 = Instant.now();
//        val builder = new StringBuilder();
//        for (var channel1 : futuress) {
//            livestream = channel1.livestream();
//            user = channel1.user();
//            if (livestream == null)
//                builder
//                    .append("\n")
//                    .append("id: ")
//                    .append(channel1.id())
//                    .append(", slug: ")
//                    .append(channel1.slug())
//                    .append(", thumbnail: ")
//                    .append("null")
//                    .append(", viewer count: ")
//                    .append(0)
//                    .append(", user id: ")
//                    .append(user.id())
//                    .append(", username: ")
//                    .append(user.name())
//                    .append(", chatroom id: ")
//                    .append(channel1.chatroom().id());
//            else
//                builder
//                    .append("\n")
//                    .append("id: ")
//                    .append(channel1.id())
//                    .append(", slug: ")
//                    .append(channel1.slug())
//                    .append(", thumbnail: ")
//                    .append(livestream.thumbnail().url())
//                    .append(", viewer count: ")
//                    .append(livestream.viewerCount())
//                    .append(", user id: ")
//                    .append(user.id())
//                    .append(", username: ")
//                    .append(user.name())
//                    .append(", chatroom id: ")
//                    .append(channel1.chatroom().id());
//        }
//        System.out.println(builder);
//        var performance2 = Instant.now();
//        log.info("finished execution in %dms".formatted( Duration.between(performance1, performance2).toMillis()));
//        log.info("executing string formatting...");
//        var performancee1 = Instant.now();
//        var string = "";
//        for (var channel : futuress) {
//            livestream = channel.livestream();
//            user = channel.user();
//            if (livestream == null)
//                string = string.concat(String.format("\nid: %d, slug: %s, thumbnail: %s, viewer count: %d, user id: %d, username: %s, chatroom id: %d", channel.id(), channel.slug(), "null", 0, user.id(), user.name(), channel.chatroom().id()));
//            else
//                string = string.concat(String.format("\nid: %d, slug: %s, thumbnail: %s, viewer count: %d, user id: %d, username: %s, chatroom id: %d", channel.id(), channel.slug(), livestream.thumbnail().url(), livestream.viewerCount(), user.id(), user.name(), channel.chatroom().id()));
//        }
//        System.out.println(string);
//        var performancee2 = Instant.now();
//        log.info("finished execution in %dms".formatted(Duration.between(performancee1, performancee2).toMillis()));
//        System.exit(0);
//        database.getAllChannels().forEach(System.out::println);
//        log.info("deleting all channels...");
//        database.deleteAllChannels();
//        log.info("deleted all channels successfully");
//        val pusher = new Pusher("eb1d5f283081a78b932c", new PusherOptions().setChannelAuthorizer(client).setCluster("us2").setHost("stats.pusher.com"));
//        pusher.connect(new ConnectionEventListener() {
//            @Override
//            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
//                log.info("connection state has been changed from %s to %s".formatted(connectionStateChange.getPreviousState() ,connectionStateChange.getCurrentState()));
//            }
//
//            @Override
//            public void onError(String message, String code, Exception e) {
//                log.warning("could not establish connection; Message: %s, Code: %s, Exception message: %s".formatted(message, code, e.getMessage()));
//            }
//        }, ConnectionState.ALL);
//        val channel = client.getChannelSync("narash");
//        val pushChannel = pusher.subscribe("chatroom." + channel.chatroom().id());
//        pushChannel.bind("message", pusherEvent -> log.info(pusherEvent.toJson()));
//        while (pusher.getConnection().getState() != DISCONNECTED){
//            TimeUnit.MILLISECONDS.sleep(100);
//        }
//       val mapper = new ObjectMapper();
//       Token token;
//       val clientR = KickClient.getRClient();
//       val defaultHeaders = Headers.of("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
//            "Accept-Encoding", "gzip, deflate, br",
//            "Accept-Language", "en-US,en;q=0.9",
//            "Authorization", "",
//            "Connection", "keep-alive",
//            "Host", "kick.com",
//            "sec-ch-ua", "\"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"",
//            "sec-ch-ua-mobile", "?0",
//            "sec-ch-ua-platform", "Windows",
//            "sec-fetch-dest", "document",
//            "sec-fetch-mode", "navigate",
//            "sec-fetch-site", "none",
//            "sec-fetch-user", "?1",
//            "upgrade-insecure-requests", "1",
//            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
//            "X-XSRF-TOKEN", "");
//       val authHeaders = new String[]{
//           "authority", "kick.com",
//           "accept", "application/json, text/plain, */*",
//           "accept-language", "en-US",
//           "authorization", "Bearer ",
//           "content-type", "application/json",
//           "origin", "https://kick.com",
//           "referer", "https://kick.com/",
//           "sec-ch-ua", """
//                           "Google Chrome";v="111", "Not(A:Brand";v="8", "Chromium";v="111""
//                       """,
//           "sec-ch-ua-mobile", "?0",
//           "sec-ch-ua-platform", "\"Windows\"",
//           "sec-fetch-dest", "empty",
//           "sec-fetch-mode", "cors",
//           "sec-fetch-site", "same-origin",
//           "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
//           "XSRF-TOKEN", ""};
////       val sClient = HttpClient.newBuilder().sslContext(SSLContext.getInstance("TLS"));
//       try(val response = clientR.newCall(new Request.Builder().headers(defaultHeaders).url(BASE_URL.url).get().build()).execute()) {
//           response.request().headers().forEach(System.out::println);
//           authHeaders[29] =  response.headers().values("set-cookie").get(2).split("[=;]")[1];
//           authHeaders[7] += authHeaders[29];
//       }
//       System.exit(0);
//       try(val response = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(BASE_URL.url.concat("kick-token-provider")).get().build()).execute()) {
//           System.out.println(authHeaders[7] = "Bearer " + response.headers().values("set-cookie").get(1).split("[=;]")[1]);
//           assert response.body() != null;
//           token = mapper.readValue(response.body().string(), Token.class);
//       }
//        try(val response = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(BASE_URL.url.concat("login")).post(RequestBody.create("""
//           {
//           "email": "%s",
//           "password": "%s",
//           "%s": "",
//           "_kick_token_valid_from": "%s"
//           }
//           """.formatted(Info.getUsername(), Info.getPassword(), token.nameFieldName(), token.encryptedValidFrom()), MediaType.parse("application/json"))).build()).execute()){
//           System.out.println(response.code());
//        } catch (IOException e) {
//           log.severe("could not pass through authorization; not authentic :copynine:" + e.getMessage());
//        }
//        try(val respose = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(API_V1.url.concat("user")).get().build()).execute()){
//            assert respose.body() != null;
//            System.out.println(respose.body().string());
//        } catch (IOException e) {
//            log.severe("could not fetch self; " + e.getMessage());
//        }
//        System.exit(0);
//        clientR.newCall(new Request.Builder().header("Authorization", "Bearer " + token).url(API_V1.url.concat("")).post(RequestBody.create("""
//
//           {
//           "chatroom_id"
//           }
//           """, MediaType.parse("application/json"))).build());


    }
}

