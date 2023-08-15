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
        val channel1= client.getChannel("dote").thenApply(channel -> toButton(channel.slug()));
        val channel2 = client.getChannel("narash").thenApply(channel -> toButton(channel.slug()));
        val channel3 = client.getChannel("quillcannon").thenApply(channel -> toButton(channel.slug()));
        val channel4 = client.getChannel("rowex").thenApply(channel -> toButton(channel.slug()));
        val channel5 = client.getChannel("krippyx").thenApply(channel -> toButton(channel.slug()));
        CompletableFuture.allOf(channel1, channel2, channel3, channel4, channel5).join();
            gridPane.add(channel1.join(), 0, 0);
            gridPane.add(channel2.join(), 1, 0);
            gridPane.add(channel3.join(), 2, 0);
            gridPane.add(channel4.join(), 3, 0);
            gridPane.add(channel5.join(), 4, 0);
//            System.out.println(channel2.get().getFont().getName());
//        val image = new ImageView(new Image(client.getLivestream("narash").join().thumbnail().url()));
//        gridPane.getChildren().add(image);

        //        val gridPane = new StackPane();
//        gridPane.setAlignment(Pos.CENTER);
//        val shape = new Rectangle(300, 100, Color.web("`#404446;"));
//        gridPane.getChildren().add(shape);
//        val fillTransition = new FillTransition(Duration.seconds(2));
//        fillTransition.setShape(shape);
//        fillTransition.setToValue(Color.web("#54626F"));
//        fillTransition.setCycleCount(FillTransition.INDEFINITE);
//        fillTransition.playFromStart();
        scene = new Scene(gridPane, 400, 800);
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.valueOf("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(new Image("Kivarino.png"));
        primaryStage.setTitle("Kivarino");
        primaryStage.show();
    }

    private Button toButton(String text){
        System.out.println(text);
        return new Button(text);
    }
}
