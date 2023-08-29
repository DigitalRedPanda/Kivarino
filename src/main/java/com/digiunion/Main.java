package com.digiunion;

import com.digiunion.database.Database;
import com.digiunion.kick.KickClient;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.Nullable;

/**
 * Main is the class where the project runs javafx within the main method
 */
@Log
public final class Main {
    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(@Nullable String[] args) throws InterruptedException {
//
        val database = Database.getInstance();
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
        log.info("deleting all channels...");
        database.deleteAllChannels();
        log.info("deleted all channels successfully");
    }
}

