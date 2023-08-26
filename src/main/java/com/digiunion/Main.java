package com.digiunion;

import com.digiunion.database.Database;
import lombok.extern.java.Log;
import lombok.val;

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
        val database = Database.getInstance();
        database.getAllChannels().forEach(System.out::println);
    }
}

