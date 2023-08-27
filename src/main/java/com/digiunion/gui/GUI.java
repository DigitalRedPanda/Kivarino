package com.digiunion.gui;

import com.digiunion.Main;
import com.digiunion.database.Database;
import com.digiunion.gui.skin.TabSkin;
import com.digiunion.kick.KickClient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Log
public class GUI extends Application {

    @Getter
    private Scene scene;
    private final KickClient client = new KickClient();

    private final Database database = Database.getInstance();
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        Main.main(null);
        val stackPane = new StackPane();
        stackPane.setStyle("""
            -fx-background-color: #36393e;
            """);
        val flow = new FlowPane();
        flow.setAlignment(Pos.TOP_LEFT);
        flow.setHgap(1);
        flow.setVgap(1);
        val channels = database.getAllChannels();
        val futures = new ArrayList<Button>(channels.size());
        for (var channel : channels) {
            val button = toButton(channel.user().name().toLowerCase());
            futures.add(button);
            flow.getChildren().add(button);
        }
        stackPane.getChildren().add(flow);
        scene = new Scene(stackPane, 600, 800);
        // load css at the root directory of the jar file
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.valueOf("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(new Image("Kivarino.png"));
        primaryStage.setTitle("Kivarino");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        client.getExecutor().execute(() -> {
            do try {
                for (var i = 0; i < futures.size(); i++) {
                    val skin = (TabSkin) futures.get(i).getSkin();
                    val livestream = client.getLivestreamSync(channels.get(i).slug());
                    if (livestream != null && !skin.getLiveCircle().isVisible()) {
                        skin.getLiveCircle().setRadius(2);
                        skin.getLiveCircle().setVisible(true);
                    } else if (livestream == null && skin.getLiveCircle().isVisible()) {
                        skin.getLiveCircle().setVisible(false);
                        skin.getLiveCircle().setRadius(0);
                    }
                }
                System.out.println("looking for live channels");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                log.severe("could not sleep live eventListener; " + e.getMessage());
            }
            while (true);
        });
    }

    private Button toButton(String text){
        return new Button(text);
    }
}
