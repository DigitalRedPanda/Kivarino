package com.digiunion.gui;

import com.digiunion.kick.KickClient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.util.concurrent.CompletableFuture;

@Log
public class GUI extends Application {

    private Scene scene;
    private KickClient client = new KickClient();

    @Override
    public void start(Stage primaryStage) {
        val gridPane = new GridPane();
        gridPane.setStyle("""
            -fx-background-color: #36393e;
            """);
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setVgap(1);
        gridPane.setHgap(1);
        val channel1 = client.getChannel("dote").thenApply(channel -> toButton(channel.user().name()));
        val channel2 = client.getChannel("narash").thenApply(channel -> toButton(channel.user().name()));
        val channel3 = client.getChannel("quillcannon").thenApply(channel -> toButton(channel.user().name()));
        val channel4 = client.getChannel("rowex").thenApply(channel -> toButton(channel.user().name()));
        CompletableFuture.allOf(channel1, channel2, channel3, channel4).join();
        try {
            gridPane.add(channel1.get(), 0, 0);
            gridPane.add(channel2.get(), 1, 0);
            gridPane.add(channel3.get(), 2, 0);
            gridPane.add(channel4.get(), 3, 0);
            System.out.println(channel2.get().getFont().getName());
        } catch (Exception e){
            switch (e.getCause().getClass().getName()){
                case "ExecutionException", "InterruptedException" -> log.severe("could not get completablefuture results; " + e.getMessage());
            }
        }
//        val gridPane = new StackPane();
//        gridPane.setAlignment(Pos.CENTER);
//        val shape = new Rectangle(300, 100, Color.web("`#404446;"));
//        gridPane.getChildren().add(shape);
//        val fillTransition = new FillTransition(Duration.seconds(2));
//        fillTransition.setShape(shape);
//        fillTransition.setToValue(Color.web("#54626F"));
//        fillTransition.setCycleCount(FillTransition.INDEFINITE);
//        fillTransition.playFromStart();
        scene = new Scene(gridPane, 200, 200);
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.valueOf("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("Kivarino.png"));
        primaryStage.setTitle("Kivarino");
        primaryStage.show();
    }

    private Button toButton(String text){
        return new Button(text);
    }
}
