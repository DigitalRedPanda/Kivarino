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
                    thumbnail VARCHAR,
                    viewer_count INT DEFAULT 0,
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
                        result.getString(3) == null ? null : new Livestream(new Thumbnail(result.getString(3)), result.getInt(4)),
                        new User(result.getInt(5), result.getString(6)),
                        new Chatroom(result.getInt(7)))
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
            val thumbnail = result.getString(3);
            if(thumbnail == null) {
                optional = Optional.of(new Channel(result.getInt(1), result.getString(2), null, new User(result.getInt(5), result.getString(6)), new Chatroom(result.getInt(7))));
            } else {
                optional = Optional.of(new Channel(result.getInt(1), result.getString(2), new Livestream(new Thumbnail(thumbnail), result.getInt(4)), new User(result.getInt(5), result.getString(6)), new Chatroom(result.getInt(7))));
            }

        } catch (SQLException e) {
            log.info("could not get %s; %s".formatted(slug, e.getMessage()));
            optional = Optional.empty();
        }
        return optional;
    }

    public boolean insertChannel(Channel channel) {
        try(val statement = connection.createStatement()) {
            val result = getStringBuilder(channel, channel.user(), channel.livestream()).append(";");
            statement.execute(result.toString());
            return true;
        } catch (SQLException e) {
            log.severe("could not insert %s; %s".formatted(channel.slug(), e.getMessage()));
            return false;
        }
    }

    private static StringBuilder getStringBuilder(Channel channel, User user, Livestream livestream) {
        StringBuilder result = new StringBuilder();
        if(livestream == null) {
            result.append("INSERT INTO channels VALUES(")
                .append(channel.id())
                .append(", '")
                .append(channel.slug())
                .append("', ")
                .append("null")
                .append(", ")
                .append(0)
                .append(", ")
                .append(user.id())
                .append(", '")
                .append(user.name())
                .append("', ")
                .append(channel.chatroom().id())
                .append(")");
        }
        else
            // "INSERT INTO channels VALUES (%d, '%s', '%s', %d, %d, '%s', %d)"
            result.append("INSERT INTO channels VALUES (")
                .append(channel.id())
                .append(", '")
                .append(channel.slug())
                .append("', '")
                .append(livestream.thumbnail().url())
                .append("', ")
                .append(livestream.viewerCount())
                .append(", ")
                .append(user.id())
                .append(", '")
                .append(user.name())
                .append("', ")
                .append(channel.chatroom().id())
                .append(")");

        return result;
    }

    public boolean insertAllChannels(List<Channel> list) {
        val first = list.get(0);
        val builder = getStringBuilder(first, first.user(), first.livestream());
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
        val livestream = channel.livestream();
        val user = channel.user();
        if(channel.livestream() == null) {
            builder.append(",(")
                .append(channel.id())
                .append(", '")
                .append(channel.slug())
                .append("', ")
                .append("null")
                .append(", ")
                .append(0)
                .append(", ")
                .append(user.id())
                .append(", '")
                .append(user.name())
                .append("', ")
                .append(channel.chatroom().id())
                .append(")");
        }
        else {
            builder.append(",(")
                .append(channel.id())
                .append(", '")
                .append(channel.slug())
                .append("', '")
                .append(livestream.thumbnail().url())
                .append("', ")
                .append(livestream.viewerCount())
                .append(", ")
                .append(user.id())
                .append(", '")
                .append(user.name())
                .append("', ")
                .append(channel.chatroom().id())
                .append(")");
        }
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
