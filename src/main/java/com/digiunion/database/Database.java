package com.digiunion.database;


import com.digiunion.kick.model.*;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
public class Database implements Closeable {

    @Getter
    private static final Connection connection;

    @Getter
    private static final Database instance = new Database();

    static {
        Connection temp;
        try {
            temp = DriverManager.getConnection("jdbc:h2:./kivarino.db");
            temp.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS channels (
                    id INT UNIQUE NOT NULL,
                    slug VARCHAR UNIQUE NOT NULL,
                    user_id INT UNIQUE NOT NULL,
                    username VARCHAR NOT NULL,
                    chatroom_id INT UNIQUE NOT NULL
                );
                """);
        } catch (SQLException e) {
            log.severe("could not load database; " + e.getMessage());
            System.exit(1);
            temp = null;
        }
        connection = temp;
    }

    public ArrayList<Channel> getAllChannels(){
        try(val statement = connection.createStatement()){
            val temp = new ArrayList<Channel>();
            val result = statement.executeQuery("SELECT * FROM channels;");
            while(result.next()) {
                temp.add(new Channel(result.getInt(1), result.getString(2),
                    null
                    ,new User(result.getInt(3), result.getString(4)),
                        new Chatroom(result.getInt(5)))
                );
            }
            return temp;
        } catch (SQLException e) {
            log.severe("could not query channels; " + e.getMessage());
            return null;
        }
    }

    public Optional<Channel> getChannel(String slug){
        Optional<Channel> optional;
        try(val statement = connection.createStatement()) {
            val result = statement.executeQuery("SELECT * FROM channels WHERE slug = '" + slug + "';");
            result.next();
            optional = Optional.of(new Channel(result.getInt(1), result.getString(2), null, new User(result.getInt(3), result.getString(4)), new Chatroom(result.getInt(5))));

        } catch (SQLException e) {
            log.info("could not get %s; %s".formatted(slug, e.getMessage()));
            optional = Optional.empty();
        }
        return optional;
    }

    public boolean insertChannel(Channel channel) {
        try(val statement = connection.createStatement()) {
            val result = getStringBuilder(channel, channel.user()).append(";");
            statement.execute(result.toString());
            return true;
        } catch (SQLException e) {
            log.severe("could not insert %s; %s".formatted(channel.slug(), e.getMessage()));
            return false;
        }
    }

    private static StringBuilder getStringBuilder(Channel channel, User user) {
        StringBuilder result = new StringBuilder();
        result.append("INSERT INTO channels VALUES(")
            .append(channel.id())
            .append(", '")
            .append(channel.slug())
            .append("', ")
            .append(user.id())
            .append(", '")
            .append(user.name())
            .append("', ")
            .append(channel.chatroom().id())
            .append(")");
            // "INSERT INTO channels VALUES (%d, '%s', '%s', %d, %d, '%s', %d)"

        return result;
    }

    public boolean insertAllChannels(List<Channel> list) {
        val first = list.get(0);
        val builder = getStringBuilder(first, first.user());
        for (var i = 1; i < list.size(); i++)
            insertChannelSafe(list.get(i), builder);
        try(val statement = connection.createStatement()){
            statement.execute(builder.toString());
            return true;
        } catch (SQLException e) {
            log.severe("could not insert channels; " + e.getMessage());
            return false;
        }
    }
//
//    @NotNull
//    private static StringBuilder getStringBuilder(Channel first) {
//        val builder = new StringBuilder();
//        val user = first.user();
//        val livestream = first.livestream();
//        if(livestream == null)
//             builder.append("INSERT INTO channels(id, slug, user_id, username, chatroom_id) VALUES(")
//                 .append(first.id())
//                 .append(", '")
//                 .append(first.slug())
//                 .append("', ")
//                 .append(user.id())
//                 .append(", '")
//                 .append(user.name())
//                 .append("', ")
//                 .append(first.chatroom().id())
//                 .append(")");
//        else
//            builder.append("INSERT INTO channels VALUES (")
//                .append(first.id())
//                .append(", '")
//                .append(first.slug())
//                .append("', '")
//                .append(livestream.thumbnail().url())
//                .append("', ").append(livestream.viewerCount())
//                .append(", ")
//                .append(user.id())
//                .append(", '")
//                .append(user.name())
//                .append("', ")
//                .append(first.chatroom().id())
//                .append(")");
//        return builder;
//    }

    private void insertChannelSafe(Channel channel, StringBuilder builder){
        val user = channel.user();
        builder.append(",(")
            .append(channel.id())
            .append(", '")
            .append(channel.slug())
            .append("', ")
            .append(user.id())
            .append(", '")
            .append(user.name())
            .append("', ")
            .append(channel.chatroom().id())
            .append(")");
    }

    public boolean deleteAllChannels(){
        try(val statement = connection.createStatement()){
            statement.execute("DELETE FROM channels;");
            return true;
        } catch (SQLException e) {
            log.severe("couldn't delete all channels; " + e.getMessage());
            return false;
        }
    }
    public boolean dropChannels(){
        try(val statement = connection.createStatement()){
            statement.execute("DROP TABLE channels;");
            return true;
        } catch (SQLException e) {
            log.severe("couldn't drop channels table; " + e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        Optional<String> catalog = Optional.empty();
        try {
            catalog = Optional.ofNullable(connection.getCatalog());
            catalog.ifPresent(s -> log.info("closing connection of " + s));
            connection.close();
        } catch (SQLException e) {
            log.severe("could not close connection of %s; %s".formatted(catalog.orElse("not found"), e.getMessage()));
        }
    }
}
