package com.digiunion.database;


import com.digiunion.kick.model.kivarino.Credentials;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.kivarino.ChannelApi;
import com.digiunion.kick.model.Chatroom;
import com.digiunion.kick.model.User;
import com.digiunion.kick.model.Account;
import com.digiunion.gui.GUI;

import java.io.Closeable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Optional;

import java.security.GeneralSecurityException;

public class Database implements Closeable {

  private static final Connection connection;

  public static final Database instance = new Database();

  static {
    Connection temp = null;
    try {
      temp = DriverManager.getConnection("jdbc:h2:./kivarino.db");
      try(Statement channels = temp.createStatement();
      Statement accounts = temp.createStatement();
      Statement credentials = temp.createStatement()) {
        channels.execute("""

          CREATE TABLE IF NOT EXISTS channels (
          id INT PRIMARY KEY,
          slug VARCHAR UNIQUE NOT NULL
          );

          """);
        accounts.execute("""

          CREATE TABLE IF NOT EXISTS accounts (
          id INT PRIMARY KEY,
          name VARCHAR UNIQUE NOT NULL
          );
          """);
        credentials.execute("""

          CREATE TABLE IF NOT EXISTS credentials (
          id INT REFERENCES accounts(id) ON DELETE CASCADE,
          refresh_token VARCHAR NOT NULL,
          access_token VARCHAR NOT NULL,
          expires_in BIGINT NOT NULL DEFAULT 7200,
          scope VARCHAR NOT NULL,
          issued_date TIMESTAMP NOT NULL,
          CONSTRAINT access_refresh_u UNIQUE(refresh_token,access_token)
          );

          """);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.printf("[\033[31mSEVERE\033[0m]could not load database; %s", e.getMessage());
      System.exit(1);
    }
    connection = temp;
  }

  public CopyOnWriteArrayList<ChannelApi> getAllChannels() throws SQLException {
    try(final Statement statement = connection.createStatement()){
      CopyOnWriteArrayList<ChannelApi> temp = new CopyOnWriteArrayList<>();
      ResultSet result = statement.executeQuery("SELECT * FROM channels;");
      while(result.next()) {
        temp.add(new ChannelApi(null,result.getInt(1),null,null, result.getString(2), null, null));
      }
      return temp;
    }
  }

  public CopyOnWriteArrayList<Account> getAllAccounts() throws SQLException {
    try(final Statement statement = connection.createStatement()){
      CopyOnWriteArrayList<Account> temp = new CopyOnWriteArrayList<>();
      ResultSet result = statement.executeQuery("SELECT * FROM accounts;");
      while(result.next()) {
        temp.add(new Account(result.getInt(1), result.getString(2)));
      }
      return temp;
    }
  }

  public Optional<Credentials> getCredsById(long account_id) {
    Optional<Credentials> optional;
    try(var statement = connection.prepareStatement("SELECT * FROM credentials WHERE id = ?;")) {
      statement.setLong(1, account_id);
      ResultSet result = statement.executeQuery();
      if(result.next()) {
        var refreshToken = GUI.cryptoService.decrypt(result.getString("refresh_token"));
        var accessToken = GUI.cryptoService.decrypt(result.getString("access_token"));
        var scope = result.getString("scope");
        var expiresIn = result.getLong("expires_in");
        var issuedDate = result.getTimestamp("issued_date");
        optional = Optional.of(new Credentials(account_id, refreshToken, scope, accessToken, expiresIn, issuedDate));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not get user's account_id %d token; %s\n", account_id, e.getMessage());
      e.printStackTrace();
      optional = Optional.empty();
    } catch (GeneralSecurityException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] (en/de)cryption failed ; %s\n", account_id, e.getMessage());
      optional = Optional.empty();
    }
    return optional;
  }

  public Optional<Credentials> getTokenByName(String name) {
    Optional<Credentials> optional;
    try(var statement = connection.prepareStatement("SELECT * FROM credentials c JOIN accounts a ON c.id = a.id WHERE name = ?;")) {
      statement.setString(1, name);
      ResultSet result = statement.executeQuery();
      result.next();
      var id = result.getLong("id");
      var refreshToken = GUI.cryptoService.decrypt(result.getString("refresh_token"));
      var accessToken = GUI.cryptoService.decrypt(result.getString("access_token"));
      var scope = result.getString("scope");
      var expiresIn = result.getLong("expires_in");
      var issuedDate = result.getTimestamp("issued_date");
      optional = Optional.of(new Credentials(id, refreshToken, scope, accessToken, expiresIn, issuedDate));
    } catch (SQLException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not get user's account %s token; %s\n", name, e.getMessage());
      optional = Optional.empty();
      e.printStackTrace();
    } catch (GeneralSecurityException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] (en/de)cryption failed of account %s's token; %s\n", name, e.getMessage());
      optional = Optional.empty();
    }
    return optional;
  }

  public boolean updateTokenByName(String name, Credentials creds) {
    try(var statement = connection.prepareStatement("UPDATE credentials SET access_token=?, refresh_token=?, issued_date=?, expires_in=? WHERE id = (SELECT id FROM accounts WHERE name = ?);")) {
      statement.setString(1, GUI.cryptoService.encrypt(creds.access_token()));
      statement.setString(2, GUI.cryptoService.encrypt(creds.refresh_token()));
      statement.setTimestamp(3, creds.issued_date());
      statement.setLong(4, creds.expiresIn());
      statement.setString(5, name);
      if(statement.executeUpdate() < 1) {
        return false;
      } else {
        return true;
      }
    } catch (SQLException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not get user's account_id %s token; %s\n", name, e.getMessage());
      return false;
    } catch (GeneralSecurityException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] (en/de)cryption failed of account %s's token; %s\n", name, e.getMessage());
      return false;
    }
  }

  public void insertCreds(long accountId, Credentials creds) {
    try(var statement = connection.prepareStatement("INSERT INTO credentials(id, refresh_token, access_token, expires_in, scope, issued_date) VALUES(?, ?, ?, ?, ?, ?);")) {
      statement.setLong(1, accountId);
      statement.setString(2, GUI.cryptoService.encrypt(creds.refresh_token()));
      statement.setString(3, GUI.cryptoService.encrypt(creds.access_token()));
      statement.setLong(4, creds.expiresIn());
      statement.setString(5, creds.scope());
      statement.setTimestamp(6, creds.issued_date());
      statement.execute();
      //var issuedDate = result.getTimestamp("issued_date");
    } catch (SQLException e) {
      System.err.printf("could not insert user's account_id %d token; %s", accountId, e.getMessage());
    } catch (GeneralSecurityException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] (en/de)cryption failed of account %d's token; %s\n", accountId, e.getMessage());
    }
  }

public void insertCredsByName(String accountName, Credentials creds) {
    try(var statement = connection.prepareStatement("INSERT INTO credentials(id, refresh_token, access_token, expires_in, scope, issued_date) VALUES((SELECT id FROM accounts WHERE name = ?), ?, ?, ?, ?, ?);")) {
      statement.setString(1, accountName);
      statement.setString(2, GUI.cryptoService.encrypt(creds.refresh_token()));
      statement.setString(3, GUI.cryptoService.encrypt(creds.access_token()));
      statement.setLong(4, creds.expiresIn());
      statement.setString(5, creds.scope());
      statement.setTimestamp(6, creds.issued_date());
      statement.execute();
      //var issuedDate = result.getTimestamp("issued_date");
    } catch (SQLException e) {
      System.err.printf("could not insert user's account_name %s token; %s", accountName, e.getMessage());
    } catch (GeneralSecurityException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] (en/de)cryption failed of account %s's token; %s\n", accountName, e.getMessage());
    }
  }

  public void insertAccount(Account acc) {
    try(var statement = connection.prepareStatement("INSERT INTO accounts VALUES(?, ?);")) {
      statement.setLong(1, acc.id());
      statement.setString(2, acc.name());
      statement.execute();
    } catch (SQLException e) {
      System.err.printf("could not insert user's account account_id %s; %s", acc.name(), e.getMessage());
    } 
  }
  public Optional<Account> getAccountByName(String name) {
    Optional<Account> optional;
    try(var statement = connection.prepareStatement("SELECT * FROM accounts a WHERE name = ?;")) {
      statement.setString(1, name);
      ResultSet result = statement.executeQuery();
      if(result.next()) {
      var id = result.getLong("id");
      optional = Optional.of(new Account(id, name));
      } else {
        optional = Optional.empty();
      }
    } catch (SQLException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not get user's account_id %s token; %s\n", name, e.getMessage());
      optional = Optional.empty();
    } 
    return optional;
  }

  public Optional<ChannelApi> getChannel(String slug){
    Optional<ChannelApi> optional;
    try(var statement = connection.prepareStatement("SELECT * FROM channels WHERE slug = ?;")) {
      statement.setString(1, slug);
      ResultSet result = statement.executeQuery();
      if(result.next()) {
      optional = Optional.of(new ChannelApi(null, result.getLong(1),null,null, result.getString(2), null, null));
      } else {
        optional = Optional.empty();
      }
    } catch (SQLException e) {
      System.err.printf("could not get %s; %s", slug, e.getMessage());
      optional = Optional.empty();
    }
    return optional;
  }

  public void insertChannel(ChannelApi channel) throws SQLException {
    try(var statement = connection.prepareStatement("INSERT INTO channels VALUES(?, ?)")) {
      statement.setLong(1, channel.broadcasterUserId());
      statement.setString(2, channel.slug());
      statement.execute();
    }
  }

  public void insertAllChannels(List<ChannelApi> list) throws SQLException {
    final ChannelApi first = list.get(0);
    final StringBuilder builder = new StringBuilder("INSERT INTO channels VALUES(?, ?)");
    // for(int i = 0; i < list.size(); i++) {
    //   builder.append(",(?, ?, ?, ?, ?)");
    // }
    builder.repeat(",(?, ?)", list.size()-1);
    try(var statement = connection.prepareStatement(builder.toString())){
      statement.setLong(1, first.broadcasterUserId());
      statement.setString(2, first.slug());

      for (var i = 1; i < list.size(); i++)   {
        var channel = list.get(i);
        var number = 5 * i;
        statement.setLong(1 + number, first.broadcasterUserId());
        statement.setString(2 + number, first.slug());
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
