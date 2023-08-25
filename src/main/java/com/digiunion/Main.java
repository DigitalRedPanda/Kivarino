package com.digiunion;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.MutableDocument;
import com.digiunion.database.DatabaseManager;
import com.digiunion.kick.KickClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import lombok.val;

/**
 * Main is the class where the project runs javafx and the main method
 */
@Log
public final class Main {
    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(String[] args) throws CouchbaseLiteException {
//        GUI.launch(args);
        val mapper = new ObjectMapper();
        val client = new KickClient();
        val database = DatabaseManager.getDatabase();
        val document = new MutableDocument();
        document.setJSON(client.getChannelJsonSync("migren2009"));
        database.getCollection("channels").save(document);
        val result = database.createQuery("SELECT slug, id, user, livestream FROM channels;").execute();
        result.forEach(channel -> System.out.println(channel.toJSON()));

    }
}

