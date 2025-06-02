package com.digiunion.database;


import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Chatroom;
import com.digiunion.kick.model.User;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database implements Closeable {

    private static final Connection connection;

    public static final Database instance = new Database();

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
            System.err.printf("[\033[31mSEVERE\033[0m]could not load database; %s", e.getMessage());
            System.exit(1);
            temp = null;
        }
        connection = temp;
    }

    public ArrayList<Channel> getAllChannels() throws SQLException {
        try(final Statement statement = connection.createStatement()){
            ArrayList<Channel> temp = new ArrayList<>();
            ResultSet result = statement.executeQuery("SELECT * FROM channels;");
            while(result.next()) {
                temp.add(new Channel(result.getInt(1), result.getString(2),
                    null
                    ,new User(result.getInt(3), result.getString(4)),
                        new Chatroom(result.getInt(5)))
                );
            }
            return temp;
        }
    }

    public Optional<Channel> getChannel(String slug){
        Optional<Channel> optional;
        try(final Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SELECT * FROM channels WHERE slug = '" + slug + "';");
            result.next();
            optional = Optional.of(new Channel(result.getInt(1), result.getString(2), null, new User(result.getInt(3), result.getString(4)), new Chatroom(result.getInt(5))));

        } catch (SQLException e) {
            System.err.printf("could not get %s; %s", slug, e.getMessage());
            optional = Optional.empty();
        }
        return optional;
    }

    public void insertChannel(Channel channel) throws SQLException {
        try(final Statement statement = connection.createStatement()) {
            final StringBuilder result = getStringBuilder(channel, channel.user()).append(";");
            statement.execute(result.toString());
        }
    }

    private StringBuilder getStringBuilder(Channel channel, User user) {
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

    public void insertAllChannels(List<Channel> list) throws SQLException {
        final Channel first = list.get(0);
        final StringBuilder builder = getStringBuilder(first, first.user());
        for (var i = 1; i < list.size(); i++)
            insertChannelSafe(list.get(i), builder);
        try(final Statement statement = connection.createStatement()){
            statement.execute(builder.toString());
        }
    }
    private void insertChannelSafe(Channel channel, StringBuilder builder){
        final User user = channel.user();
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

    public void deleteChannel(String slug) throws SQLException {
        try(final Statement statement = connection.createStatement()){
            final StringBuilder builder = new StringBuilder("DELETE FROM channels WHERE slug = '").append(slug).append("';");
            statement.execute(builder.toString());
        }
    }

    public void deleteAllChannels() throws SQLException {
        try(final Statement statement = connection.createStatement()){
            statement.execute("DELETE FROM channels;");
        }
    }
    public void dropChannels() throws SQLException {
        try(final Statement statement = connection.createStatement()){
            statement.execute("DROP TABLE channels;");
        }
    }

    @Override
    public void close() {
        Optional<String> catalog = Optional.empty();
        try {
            catalog = Optional.ofNullable(connection.getCatalog());
            catalog.ifPresent(s -> System.out.printf("[\033[34mINFO\033[0m] closing connection of %s\n", s));
            connection.close();
        } catch (SQLException e) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not close connection of %s; %s\n", catalog.orElse("not found"), e.getMessage());
        }
    }
}
