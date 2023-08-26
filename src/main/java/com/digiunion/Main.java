package com.digiunion;

import com.digiunion.database.Database;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import lombok.extern.java.Log;
import lombok.val;

import java.util.ArrayList;

/**
 * Main is the class where the project runs javafx within the main method
 */
@Log
public final class Main {
    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(String[] args) {
//
        val client = new KickClient();
        try (val database = new Database()) {
            database.getAllChannels().forEach(System.out::println);
            val array = new String[]{
                "dote", "narash", "quillcannon", "rowex", "rustytheowl", "abodrp", "sadmadladsalman", "migren2009"
            };
            val list = new ArrayList<Channel>(array.length);
            for (var s : array) {
                list.add(client.getChannelSync(s));
            }
            database.insertAllChannels(list);
            database.getAllChannels().forEach(System.out::println);
            database.deleteAllChannels();
        }
        System.exit(0);
    }
}

