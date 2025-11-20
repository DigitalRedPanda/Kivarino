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
import java.util.concurrent.CopyOnWriteArrayList;
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
            e.printStackTrace();
            System.err.printf("[\033[31mSEVERE\033[0m]could not load database; %s", e.getMessage());
            System.exit(1);
            temp = null;
        }
        connection = temp;
    }

    public CopyOnWriteArrayList<Channel> getAllChannels() throws SQLException {
        try(final Statement statement = connection.createStatement()){
            CopyOnWriteArrayList<Channel> temp = new CopyOnWriteArrayList<>();
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
        try(var statement = connection.prepareStatement("SELECT * FROM channels WHERE slug = '?';")) {
            statement.setString(1, slug);
            ResultSet result = statement.executeQuery();
            result.next();
            optional = Optional.of(new Channel(result.getInt(1), result.getString(2), null, new User(result.getInt(3), result.getString(4)), new Chatroom(result.getInt(5))));

        } catch (SQLException e) {
            System.err.printf("could not get %s; %s", slug, e.getMessage());
            optional = Optional.empty();
        }
        return optional;
    }

    public void insertChannel(Channel channel) throws SQLException {
        try(var statement = connection.prepareStatement("INSERT INTO channels VALUES(?, ?, ?, ?, ?)")) {
            statement.setInt(1, channel.id());
            statement.setString(2, channel.slug());
            statement.setInt(3, channel.user().id());
            statement.setString(4, channel.user().name());
            statement.setInt(5, channel.chatroom().id());
            statement.execute();
        }
    }

    public void insertAllChannels(List<Channel> list) throws SQLException {
        final Channel first = list.get(0);
        final StringBuilder builder = new StringBuilder("INSERT INTO channels VALUES(?, ?, ?, ?, ?)").repeat(",(?, ?, ?, ?, ?)", list.size() - 1);
        try(var statement = connection.prepareStatement(builder.toString())){
          statement.setInt(1, first.id());
          statement.setString(2, first.slug());
          statement.setInt(3, first.user().id());
          statement.setString(4, first.user().name());
          statement.setInt(5, first.chatroom().id());

          for (var i = 1; i < list.size(); i++)   {
            var channel = list.get(i);
            var number = 5 * i;
            statement.setInt(1 + number, channel.id());
            statement.setString(2 + number, channel.slug());
            statement.setInt(3 + number, channel.user().id());
            statement.setString(4 + number, channel.user().name());
            statement.setInt(5 + number, channel.chatroom().id());
      }
            statement.execute();
        }
    }
    public void deleteChannelBySlug(String slug) throws SQLException {
        try(var statement = connection.prepareStatement("DELETE FROM channels WHERE slug = ?;")){
            statement.setString(1, slug);
            if(statement.executeUpdate() > 0) {
              System.out.printf("[\033[34mINFO\033[0m] channel %s has been deleted\n", slug);
            } else {
              System.out.printf("[\033[34mINFO\033[0m] channel %s has not been deleted\n", slug);
            }
        }
    }

    public void deleteChannelByUsername(String username) throws SQLException {
        try(var statement = connection.prepareStatement("DELETE FROM channels WHERE username = ?;")){
            statement.setString(1, username);
            if(statement.executeUpdate() > 0) {
              System.out.printf("[\033[34mINFO\033[0m] channel %s has been deleted\n", username);
            } else {
              System.out.printf("[\033[34mINFO\033[0m] channel %s has not been deleted\n", username);
            }
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
