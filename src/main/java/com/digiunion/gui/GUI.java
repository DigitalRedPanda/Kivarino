package com.digiunion.gui;

import com.digiunion.Main;
import com.digiunion.database.Database;
import com.digiunion.gui.component.Tab;
import com.digiunion.gui.skin.AddButtonSkin;
import com.digiunion.gui.skin.TabSkin;
import com.digiunion.kick.KickClient;
import javafx.application.Application;
import javafx.scene.Scene;
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

    @Getter
    private static final Image icon = new Image("Kivarino.png");
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        Main.main(null);
        val stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #36393e;");
        val addButton = new Tab("+");
        addButton.setId("add-button");
        val flow = new FlowPane();
        //this is supposed to be the default setting
//        flow.setAlignment(Pos.TOP_LEFT);
        flow.setHgap(1);
        flow.setVgap(1);
        addButton.setSkin(new AddButtonSkin(addButton, flow));
        val channels = database.getAllChannels();
        val buttons = new ArrayList<Tab>(channels.size());
        for (var channel : channels) {
            val button = toButton(channel.user().name().toLowerCase());
            buttons.add(button);
            flow.getChildren().add(button);
        }
        flow.getChildren().add(addButton);

        stackPane.getChildren().add(flow);
        scene = new Scene(stackPane, 600, 800);
        // load css at the root directory of the jar file
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.valueOf("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Kivarino");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        client.getExecutor().execute(() -> {
            do try {
                for (var i = 0; i < buttons.size(); i++) {
                    val skin = (TabSkin) buttons.get(i).getSkin();
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
    private Tab toButton(String text){
        val button = new Tab(text);
        button.setId("tab");
        return button;
    }
}
