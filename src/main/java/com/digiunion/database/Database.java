package com.digiunion.database;


import com.digiunion.kick.model.*;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log
public class Database implements Closeable {

    @Getter
    private static final Connection connection;

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

    public boolean insertAllChannels(List<Channel> list) {
        val builder = getStringBuilder(list.get(0));
        for (var i = 1; i < list.size(); i++) {
            insertChannelSafe(list.get(i), builder);
        }
        try(val statement = connection.createStatement()){
            return statement.execute(builder.toString());
        } catch (SQLException e) {
            log.severe("could not insert channels; " + e.getMessage());
            return false;
        }
    }

    @NotNull
    private static StringBuilder getStringBuilder(Channel first) {
        StringBuilder builder;
        val user = first.user();
        val livestream = first.livestream();
        if(livestream == null)
            builder = new StringBuilder("INSERT INTO channels VALUES(%d, '%s', null, 0, %d, '%s', %d)".formatted(first.id(), first.slug(), user.id(), user.name(), first.chatroom().id()));
        else
            builder = new StringBuilder("INSERT INTO channels VALUES(%d, '%s', '%s', %d, %d, '%s', %d)".formatted(first.id(), first.slug(), livestream.thumbnail(), livestream.viewerCount(), user.id(), user.name(), first.chatroom().id()));

        return builder;
    }

    private void insertChannelSafe(Channel channel, StringBuilder builder){
        val livestream = channel.livestream();
        val user = channel.user();
        if(channel.livestream() == null)
            builder.append(",(%d, '%s', null, 0, %d, '%s', %d)".formatted(channel.id(), channel.slug(), user.id(), user.name(), channel.chatroom().id()));
        else
            builder.append(",(%d, '%s', '%s', %d, %d, '%s', %d)".formatted(channel.id(), channel.slug(), livestream.thumbnail(), livestream.viewerCount(), user.id(), user.name(), channel.chatroom().id()));
    }

    public boolean deleteAllChannels(){
        try(val statement = connection.createStatement()){
            return statement.execute("delete FROM channels;");
        } catch (SQLException e) {
            log.severe("couldn't delete all channels; " + e.getMessage());
            return false;
        }
    }
    public boolean dropChannels(){
        try(val statement = connection.createStatement()){
            return statement.execute("DROP TABLE channels;");
        } catch (SQLException e) {
            log.severe("couldn't drop channels table; " + e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
    }
}
