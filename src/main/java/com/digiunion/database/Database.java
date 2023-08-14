package com.digiunion.database;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Log
public class Database implements Closeable {

    @Getter
    private final Connection connection;


    public Database() throws SQLException {
        val dotenv = Dotenv.configure()
            .filename("db_info")
            .load();
        val properties = new Properties();
        properties.put("username",dotenv.get("USERNAME"));
        properties.put("password",dotenv.get("PASSWORD"));
        connection = DriverManager.getConnection("jdbc:h2:file:kivarino.db", properties);
        log.info("database connection has been established " + connection.getCatalog());
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.severe("could not close connection; " + e.getMessage());
        }
    }
}
