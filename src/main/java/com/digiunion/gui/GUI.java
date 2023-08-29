package com.digiunion.gui;

import com.digiunion.database.Database;
import com.digiunion.gui.skin.AddButtonSkin;
import com.digiunion.gui.skin.RemoveButtonSkin;
import com.digiunion.gui.skin.TabSkin;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Livestream;
import javafx.application.Application;
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
    private static Scene scene;
    @Getter
    private static final KickClient client = new KickClient();
    @Getter
    private static final Database database = Database.getInstance();
    @Getter
    private static final Image icon = new Image("Kivarino.png");
    @Getter
    private static final ArrayList<Channel> channels = database.getAllChannels();
    @Getter
    private static final ArrayList<Button> buttons = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        database.deleteAllChannels();
        val stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #36393e;");
        val addButton = new Button("+");
        addButton.setId("add-button");
        val flow = new FlowPane();
        //this is supposed to be the default setting
//        flow.setAlignment(Pos.TOP_LEFT);
        flow.setHgap(1);
        flow.setVgap(1);
        addButton.setSkin(new AddButtonSkin(addButton, flow));
        for (var channel : channels) {
            val button = toButton(channel.slug());
            val remover = new Button("x");
            remover.setSkin(new RemoveButtonSkin(button, remover, flow));
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
            while (true) try {
                Livestream livestream;
                TabSkin skin;
                for (var i = 0; i < buttons.size(); i++) {
                    livestream = channels.get(i).livestream();
                    skin = (TabSkin) buttons.get(i).getSkin();
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
        });
    }
    private Button toButton(String text){
        val button = new Button(text);
        button.setId("tab");

        return button;
    }
}
