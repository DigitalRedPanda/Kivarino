package com.digiunion.gui.component;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.application.Platform;

import com.digiunion.gui.GUI;

import java.util.Optional;

import com.digiunion.kick.util.irc.IRCMessage;

public class Chat extends Region {
  private ScrollPane chatView;
  private VBox vBox;
  private long channelId;
  public Chat(double width, double height, long channelId) {
    this.channelId = channelId;
    if(getStylesheets().add("chat.css")) {
      System.out.println("[\033[34mINFO\033[0m] chat.css has been loaded");
    } else {

      System.out.println("[\033[33mWARNING\033[0m] chat.css has been loaded");
    }
    this.setFocusTraversable(false);
    this.getStyleClass().add("chat-box");
    VBox layout = new VBox();
    chatView = new ScrollPane();
    vBox = new VBox();

    chatView.setContent(vBox);
    var textField = new TextField();

    textField.setPromptText("I am looking at you O-O");
    textField.getStyleClass().add("message-textfield");
    textField.setOnAction(e -> {
    String message = textField.getText();
    if (message == null || message.isBlank()) return;
    
    textField.clear();
    
    GUI.client.getExecutor().execute(() -> {
        try {
            GUI.client.postMessage(GUI.tkn.get(), this.channelId, message).join();
        } catch (Exception ex) {
            Platform.runLater(() -> textField.setText(message));
        }
    });
});

    VBox.setVgrow(chatView, Priority.ALWAYS);

    layout.getChildren().addAll(chatView, textField);
    getChildren().add(layout);

    layout.prefWidthProperty().bind(widthProperty());
    layout.prefHeightProperty().bind(heightProperty());
    textField.prefWidthProperty().bind(widthProperty());
    chatView.pannableProperty().set(true);
  }

  public void addMessage(IRCMessage msg) {
    //System.out.printf("[\033[34mINFO\033[0m] [%s:%s] :%s\n", msg.channel(), msg.prefix().nick(), msg.message());
    Platform.runLater(() -> {
      var hBox = new HBox();
      hBox.setPadding(new Insets(2));
      var channel = new Label(msg.tags().get("sender_username"));
      channel.setWrapText(true);
      channel.maxWidthProperty().bind(chatView.widthProperty());
      channel.setStyle(String.format("-fx-text-fill: %s;", Optional.ofNullable(msg.tags().get("color")).orElse("#ffffff")));
      var message = new Label(": " + msg.message());
      message.setWrapText(true);
      message.maxWidthProperty().bind(chatView.widthProperty());
      hBox.getChildren().addAll(channel, message);
      vBox.getChildren().add(hBox);
      chatView.setVvalue(1.0);
      chatView.setFitToWidth(true);          
      chatView.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);   
    });
  }
}
