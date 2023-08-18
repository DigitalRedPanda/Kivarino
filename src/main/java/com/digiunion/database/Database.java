package com.digiunion.database;


import com.digiunion.kick.model.Channel;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Database implements Closeable {

    @Getter
    private final Connection connection;


    public Database() {
        Connection connection1;
        val dotenv = Dotenv.configure()
            .filename("db_info")
            .load();
        val properties = new Properties();
        properties.put("username",dotenv.get("USERNAME"));
        properties.put("password",dotenv.get("PASSWORD"));
        try {
            connection1 = DriverManager.getConnection("jdbc:h2:file:kivarino.db", properties);
            log.info("database connection has been established {}", connection1.getCatalog());
            connection1.createStatement().execute("""
                CREATE TABLE IF NOT EXIST channels(
                id INT PRIMARY KEY,
                slug VARCHAR(30) UNIQUE NOT NULL,
                );
                CREATE TABLE IF NOT EXIST user(
                id INT PRIMARY KEY,
                name VARCHAR(30) NOT NULL
                );
                CREATE TABLE livestream(
                thumbnail STRING,

                );
                """);
        } catch (SQLException e) {
            log.error("could not initialize database connection; {}", e.getMessage());
            connection1 = null;
        }
        connection = connection1;
        log.info("database has been initialized");
    }
    public boolean addChannel(Channel channel){
        try(val statement = connection.createStatement()){
            statement.execute("""
               INSERT INTO channels VALUES(%d,'%s');
            """.formatted(channel.id(),channel.slug()));
            log.info("inserted {} channel successfully", channel.slug());
            return true;
        } catch (SQLException e) {
            log.warn("could not insert {} channel; {}", channel.slug(), e.getMessage());
            return false;
        }
    }

    public List<Channel> getAllChannels(){
        try(val statement = connection.createStatement()) {
            val list = new ArrayList<Channel>();
            val result = statement.executeQuery("SELECT * FROM channels;");
            while(result.next()){
                list.add(null);
            }
            return list;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("could not close connection; {}", e.getMessage());
        }
    }
}
