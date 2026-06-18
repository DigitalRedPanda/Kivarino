package com.digiunion.gui;

import com.digiunion.database.Database;
import com.digiunion.crypto.CryptoService;
import com.digiunion.gui.component.Tab;
import com.digiunion.gui.component.Chat;
import com.digiunion.gui.skin.AddButtonSkin;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.websocket.KivarinoListener;
import com.digiunion.kick.model.Account;
import com.digiunion.kick.model.UserData;
import com.digiunion.kick.model.kivarino.Stream;
import com.digiunion.kick.model.kivarino.Credentials;
import com.digiunion.kick.model.kivarino.ChannelApi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.stage.WindowEvent;


import java.sql.Timestamp;
import java.sql.SQLException;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

import java.net.http.WebSocket;

import java.time.Duration;
import java.time.Instant;

public class GUI extends Application {

  public static Scene scene;
  public static AtomicReference<Credentials> tkn = new AtomicReference<>();
  public static final CryptoService cryptoService;
  public static final KickClient client = new KickClient();
  public static final Database database = Database.instance;
  private Account activeAccount;
  public static Image icon;
  public static final FlowPane flow = new FlowPane();
  public static final CopyOnWriteArrayList<ChannelApi> channels;
  public static final Map<String, Chat> channelsChats;
  public static Stage primaryStage;
  public static BorderPane borderPane;

  static {
    CopyOnWriteArrayList<ChannelApi> channels1;
    CryptoService crypto = null;
    channelsChats = new ConcurrentHashMap<>();
    try {
      channels1 = new CopyOnWriteArrayList<>(database.getAllChannels().stream().map(c ->  {
        return new ChannelApi(null, c.broadcasterUserId(), null, null, c.slug(), null, null);
      }).collect(Collectors.toList()).toArray(new ChannelApi[0]));
    } catch (SQLException e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not load channels; %s\n", e.getMessage());
      channels1 = new CopyOnWriteArrayList<>();
    }
    channels = channels1;
    try {
      crypto = new CryptoService("Hello".toCharArray());
    } catch(Exception e) {
      System.err.printf("[\033[31mSEVERE\033[0m] could not startup crypto service; %s\n%s\n", e.getMessage(), Arrays.stream(e.getStackTrace()).map(v -> String.format("%s.%s:%d", v.getClassName(), v.getMethodName(), v.getLineNumber())).reduce((t, b) -> t.concat(t + '\n' + b)));
      System.exit(1);
    }
    cryptoService = crypto;
  }
  public static final CopyOnWriteArrayList<Tab> tabs = new CopyOnWriteArrayList<>();
  @Override
  public void start(Stage primaryStage) {
    channels.stream().forEach(c -> {
      var chatBox = new Chat(primaryStage.getWidth(), 300, c.broadcasterUserId());
      chatBox.prefWidthProperty().bind(primaryStage.widthProperty());
      chatBox.prefHeightProperty().bind(primaryStage.heightProperty().subtract(flow.heightProperty()));
      channelsChats.put(c.slug(),chatBox);
    });
    icon = new Image("Kivarino.png");
    GUI.primaryStage = primaryStage;
    borderPane = new BorderPane();
    borderPane.setStyle("-fx-background-color: #36393e;");
    final Button addButton = new Button("+");
    addButton.setId("add-button");
    //this is supposed to be the default setting
    //        flow.setAlignment(Pos.TOP_LEFT);
    flow.setHgap(0);
    flow.setVgap(1);
    addButton.setSkin(new AddButtonSkin(addButton, flow));
    if(!channels.isEmpty()) {
      for (final ChannelApi channel : channels) {
        final Tab tab = new Tab(channel.slug());
        tabs.add(tab);
        flow.getChildren().add(tab);
      }
      tabs.get(0).requestFocus();
    }
    flow.getChildren().add(addButton);
    borderPane.setTop(flow);
    scene = new Scene(borderPane, 600, 800);
    // load css at the root directory of the jar file
    scene.getStylesheets().add("main.css");
    scene.setFill(Color.web("#36393e"));
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(400);
    primaryStage.setMinHeight(800);
    primaryStage.getIcons().add(icon);
    primaryStage.setTitle("Kivarino");
    primaryStage.show();
    primaryStage.setOnCloseRequest(closeEvent -> {
      primaryStage.close();
      System.exit(0);
    });

    try {

      System.out.println("[\033[34mINFO\033[0m] fetching account");
      var tmpop = database.getAllAccounts();
      if(!tmpop.isEmpty()) {
        System.out.println("[\033[34mINFO\033[0m] loading account");
        activeAccount = tmpop.get(0);
      }
    } catch(SQLException e){
      System.out.printf("[\033[31SEVERE\033[0m] didn't find ya token bub; %s\n", e.getMessage());
    }
    if(activeAccount != null && activeAccount.name() != null) {
      System.out.println("[\033[34mINFO\033[0m] loading token");
      var tmpTkn = database.getTokenByName(activeAccount.name());
      if(tmpTkn.isPresent()) {
        tkn.set(tmpTkn.get());
      }
    }


    scene.setOnKeyPressed(e -> {
      System.out.println("[\033[34mINFO\033[0m] o here we go!");
      if(e.getCode() == KeyCode.T) {
        System.out.println("[\033[34mINFO\033[0m] key has been pressed");
        var subStage = new Stage(StageStyle.UTILITY);
        var label = new Label("Get at https://kivarino.online/authorize");
        var textThing = new PasswordField();
        textThing.setMaxWidth(200);
        textThing.setAccessibleText("Don't worry, not stealing your data (no promises), though it might get stolen");
        textThing.getStyleClass().add("kick-textfield");
        textThing.setOnAction(ev -> {
          var token = textThing.getCharacters().toString().split(";");
          UserData result;
          try {
            result = client.introspect(token[0])
            .thenCompose(res -> {
              if(res != null && res.data().active()) {
                return client.getUserByToken(token[0]);
              } else {
                return client.refresh(tkn.get()).thenCompose(r -> {
                  tkn.set(r);
                  return client.getUserByToken(r.access_token());
                });
              }
            }).get();
            var userD = result.data()[0];
            activeAccount = new Account(userD.userId(), userD.name());
            System.out.printf("[\033[34mINFO\033[0m] account fetched: [%d,%s]\n", activeAccount.id(), activeAccount.name());
            if(activeAccount == null) {
              database.insertAccount(activeAccount);
              System.out.println("[\033[34mINFO\033[0m] pause...");
              if(tkn.get().access_token().equals(token[0])) {
                if(database.updateTokenByName(activeAccount.name(), tkn.get())) {
                  subStage.close();
                } else {
                  database.insertCreds(activeAccount.id(), tkn.get());
                  System.out.println("[\033[31mSEVERE\033[0m] token refresh failed\n");
                  subStage.close();
                }
              }
            } else {

              System.out.println("[\033[31mSEVERE\033[0m] weird\n");
            }
          } catch(InterruptedException | ExecutionException ex) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not verify token; %s\n", ex.getMessage());
            ex.printStackTrace();
          }
        });
        final VBox everlastingPain = new VBox(5);
        everlastingPain.setStyle("-fx-background-color: #36393e;");
        everlastingPain.setAlignment(Pos.CENTER);
        everlastingPain.getChildren().addAll(label, textThing);
        final Scene deepState = new Scene(everlastingPain,500, 100);
        deepState.getStylesheets().add("main.css");
        subStage.setTitle("Credentials Setup");
        subStage.setScene(deepState);
        subStage.show();

      }
    });
    AtomicBoolean condition = new AtomicBoolean(false);
    if(activeAccount == null || tkn.get() == null) {
      System.out.println("[\033[34mINFO\033[0m] key has been pressed");
      var subStage = new Stage(StageStyle.UTILITY);
      var label = new Label("Get at https://kivarino.online/authorize");
      var textThing = new PasswordField();
      textThing.setMaxWidth(200);
      textThing.setAccessibleText("Don't worry, not stealing your data (no promises), though it might get stolen");
      textThing.getStyleClass().set(0,"kick-textfield");
      textThing.setOnAction(ev -> {
        if(!textThing.getCharacters().isEmpty() ) {
          var token = textThing.getCharacters().toString().split(";");
          UserData result;
          try {
            result = client.introspect(token[0])
            .thenCompose(res -> {
              if(res != null && res.data().active()) {
                return client.getUserByToken(token[0]);
              } else {
                return client.refresh(new Credentials(0, token[1], null, token[0], 0, null)).thenCompose(r -> {

                  tkn.set(new Credentials(r.account_id(), r.refresh_token(), r.scope(), r.access_token(), r.expiresIn(), Timestamp.from(Instant.now())));
                  return client.getUserByToken(r.access_token());
                });
              }
            }).get();
            var userD = result.data()[0];
            activeAccount = new Account(userD.userId(), userD.name());
            System.out.printf("[\033[34mINFO\033[0m] account fetched: [%d,%s]\n", activeAccount.id(), activeAccount.name());
            database.insertAccount(activeAccount);
            System.out.println("[\033[34mINFO\033[0m] pause...");
            if(tkn.get() == null) {
              tkn.set(database.getTokenByName(activeAccount.name()).orElse(new Credentials(userD.userId(), token[1], "", token[0], 7200, Timestamp.from(Instant.now()))));
            }
            if(database.updateTokenByName(activeAccount.name(), tkn.get())) {
              condition.set(true);
              subStage.close();
            } else {
              database.insertCredsByName(activeAccount.name(), tkn.get());
              System.out.println("[\033[31mSEVERE\033[0m] token refresh failed\n");
              condition.compareAndSet(false, true);
              subStage.close();
            }

          } catch(InterruptedException | ExecutionException ex) {
            System.err.printf("[\033[31mSEVERE\033[0m] could not verify token; %s\n", ex.getMessage());
            ex.printStackTrace();
          }
        }
      });
      final VBox everlastingPain = new VBox(5);
      everlastingPain.setStyle("-fx-background-color: #36393e;");
      everlastingPain.setAlignment(Pos.CENTER);
      everlastingPain.getChildren().addAll(label, textThing);
      final Scene deepState = new Scene(everlastingPain,500, 100);
      deepState.getStylesheets().add("main.css");
      subStage.setTitle("Credentials Setup");
      subStage.setScene(deepState);
      subStage.show();


    } else {
      System.out.printf("[\033[34mINFO\033[0m] token found for %s\n", activeAccount.name());
      tkn.set(database.getTokenByName(activeAccount.name()).get());
      try {
        Credentials t = client.introspect(tkn.get().access_token()).thenCompose(introspection -> {
          if(!introspection.data().active()) {
            return client.refresh(tkn.get());
          } else {
            return CompletableFuture.completedFuture(tkn.get());
          }
        }).get();
        if(!t.tokensEquals(tkn.get())) {
          tkn.set(new Credentials(t.account_id(), t.refresh_token(), t.scope(), t.access_token(), t.expiresIn(), Timestamp.from(Instant.now())));
          database.updateTokenByName(activeAccount.name(), tkn.get());
        }
        condition.set(true);
      } catch(InterruptedException | ExecutionException e) {
        System.out.printf("[\033[31mSEVERE\033[0m] could not load token; %s\n", e.getMessage());
      }
    }
    client.getExecutor().execute(() -> {
      try {
        while(!condition.get()) Thread.sleep(500);
        WebSocket newSock = client.connect(new KivarinoListener(activeAccount)).join();
WebSocket oldSock = client.webSocket.getAndSet(newSock);
if (oldSock != null) oldSock.abort();      } catch(Exception e){
        System.out.printf("[\033[31SEVERE\033[0m] didn't find connect ya webSocket bub; %s\n", e.getMessage());
      }
    });
    // var chatBox = new Chat(primaryStage.getWidth(), 300);
    // chatBox.prefWidthProperty().bind(primaryStage.widthProperty());
    // chatBox.prefHeightProperty().bind(primaryStage.heightProperty().subtract(flow.heightProperty()));
    // borderPane.setCenter(chatBox);
    //
    client.getExecutor().execute(() -> {
      try{
        while(!condition.get()) {
          System.out.printf("[\033[34mINFO\033[0m] ");
          Thread.sleep(500);
        };
      } catch(Exception e) {

      }
      
      do try {
        if(tkn.get() != null){
          Thread.sleep(Duration.ofSeconds(5));
          var tknOpt = database.getTokenByName(activeAccount.name());
          if(tknOpt.isPresent()) {
            var newTkn = client.introspect(tkn.get().access_token())
            .thenCompose(r -> {
              if(!r.data().active()) {
                return client.refresh(tkn.get());
              } else {
                return CompletableFuture.completedFuture(tkn.get());
              }
            }).get();
            if(newTkn != tkn.get()) {
              database.updateTokenByName(activeAccount.name(), newTkn);
              tkn.set(new Credentials(newTkn.account_id(), newTkn.refresh_token(), newTkn.scope(), newTkn.access_token(), newTkn.expiresIn(), Timestamp.from(Instant.now())));
            };
          }

          if(tabs.size() > 0) {
            final CopyOnWriteArrayList<ChannelApi> safetyMeasure = channels;
            final CopyOnWriteArrayList<Tab> safetyMeasure2 = tabs;
            var res = new CopyOnWriteArrayList<ChannelApi> (Arrays.stream(client.getChannelsBySlug(tkn.get(), safetyMeasure.stream().map(ChannelApi::slug).toList().toArray(new String[0])).get()).toList().toArray(new ChannelApi[0]));

            for (var i = 0; i < res.size(); i++) {
              final int idx = i;
              //System.out.println(safetyMeasure.get(i));
              Platform.runLater( () -> {

                Stream livestream;
                Circle circle;

                livestream = res.get(idx).stream()/*)*/;
                circle = safetyMeasure2.stream().filter(c -> c.text.textProperty().get().equals(res.get(idx).slug())).toList().get(0).liveCircle;
                if (livestream.isLive() && !circle.isVisible()) {
                  circle.setRadius(2);
                  circle.setVisible(true);
                } else if (!livestream.isLive() && circle.isVisible()) {
                  circle.setVisible(false);
                  circle.setRadius(0);
                }
              });
            }
            System.out.println("[\033[34mINFO\033[0m]looking for live channels");
          }
          TimeUnit.SECONDS.sleep(15);
        } else {
          System.out.printf("god fuckin' damn it\n");
        }
        try {
          Thread.sleep(1000);
        } catch(InterruptedException e) {
          System.out.printf("[\033[33mWARNING\033[0m] oops, couldn't sleep enough\n");
        }
      } catch(Exception e) {
        System.err.printf("[\033[31mSEVERE\033[0m] could not sleep live eventListener; %s\n", e.getMessage());
      } finally {
        try{TimeUnit.SECONDS.sleep(5);} catch(Exception e) {}
      }
      // } catch (InterruptedException | ExecutionException  e) {
      //   System.err.printf("[\033[31mSEVERE\033[0m] could not sleep live eventListener; %s\n", e.getMessage());
      // } catch(IndexOutOfBoundsException e) {
      //   System.out.printf("[\033[33mWARNING\033[0m] oops, race condition\n");
      // }
      while (true);


    });
    primaryStage.setOnCloseRequest(ev -> {
      CompletableFuture<WebSocket>[] completableFutures = new CompletableFuture[GUI.channels.size()];
      for (int i = 0; i < completableFutures.length; i++) {
        completableFutures[i] = client.webSocket.get().sendText("PART #" + GUI.channels.get(i).slug(),true);
      }
      CompletableFuture.allOf(completableFutures).join();
      client.webSocket.get().sendClose(WebSocket.NORMAL_CLOSURE, "connection terminated");
    });
    // client.getExecutor().submit(() -> {
    //
    //   try {
    //     while (true) {
    //       if(tkn.get() != null){
    //         Thread.sleep(Duration.ofSeconds(5));
    //         var tknOpt = database.getTokenByName(activeAccount.name());
    //         if(tknOpt.isPresent()) {
    //           var newTkn = client.introspect(tkn.get().access_token())
    //           .thenCompose(r -> {
    //             if(!r.data().active()) {
    //               return client.refresh(tkn.get());
    //             } else {
    //               return CompletableFuture.completedFuture(tkn.get());
    //             }
    //           }).get();
    //           if(newTkn != tkn.get()) {
    //             database.updateTokenByName(activeAccount.name(), newTkn);
    //             tkn.set(new Credentials(newTkn.account_id(), newTkn.refresh_token(), newTkn.scope(), newTkn.access_token(), newTkn.expiresIn(), Timestamp.from(Instant.now())));
    //           };
    //         }
    //       }
    //       Thread.sleep(1000);
    //     }
    //
    //   } catch(InterruptedException | ExecutionException e) {
    //
    //   }
    //
    //   try{Thread.sleep(1000);} catch(InterruptedException ex) {}
    // });
  }
  public static void main(String[] args) {
    GUI.launch(args);
  }

}
