package com.digiunion.database;


import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.Closeable;

@Log
public class DatabaseManager implements Closeable {
    @Getter
    private static final Database database;

//    private static final File directory = new File("kivarino.json");

    static {
        Database database1;
        log.info("initializing couchbase...");
        CouchbaseLite.init();
        log.info("initialized successfully");
        try {
            log.info("loading database...");
            database1 = new Database("kivarino");
            database1.createCollection("channels");
        } catch (CouchbaseLiteException e) {
            log.severe("could not load database; " + e.getMessage());
            database1 = null;
        }
        database = database1;
        log.info("database has been loaded successfully");
    }

    @Override
    public void close() {
        try {
            database.close();
        } catch (CouchbaseLiteException e) {
            log.severe("could not close database; " + e.getMessage());
        }
    }
}
