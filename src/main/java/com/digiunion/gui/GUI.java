package com.digiunion.gui;

import com.digiunion.database.Database;
import com.digiunion.gui.component.Tab;
import com.digiunion.gui.skin.AddButtonSkin;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Log
public class GUI extends Application {

    public static Scene scene;

    public static final KickClient client = new KickClient();
    public static final Database database = Database.instance;
    public static final Image icon = new Image("Kivarino.png");
    public static final FlowPane flow = new FlowPane();
    public static final ArrayList<String> channels = new ArrayList<>();

    static {
        ArrayList<Channel> channels1;
        try {
            channels1 = database.getAllChannels();
        } catch (SQLException e) {
            log.severe("could not load channels; " + e.getMessage());
            channels1 = new ArrayList<>();
        }
        for (val channel : channels1)
            channels.add(channel.slug());
    }

    public static final ArrayList<Tab> tabs = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) {
        val stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #36393e;");
        val addButton = new Button("+");
        addButton.setId("add-button");
        //this is supposed to be the default setting
//        flow.setAlignment(Pos.TOP_LEFT);
        flow.setHgap(1);
        flow.setVgap(1);
        addButton.setSkin(new AddButtonSkin(addButton, flow));
        if(!channels.isEmpty()) {
            for (val channel : channels) {
                val button = new Tab(channel);
                flow.getChildren().add(button);
            }
            tabs.get(0).requestFocus();
        }
        flow.getChildren().add(addButton);
        stackPane.getChildren().add(flow);
        scene = new Scene(stackPane, 600, 800);
        // load css at the root directory of the jar file
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.web("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Kivarino");
//        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
        primaryStage.setOnCloseRequest(closeEvent -> {
            primaryStage.close();
            System.exit(0);
        });
        client.getExecutor().execute(() -> {
            while (true) try {
                Livestream livestream;
                Circle circle;
                for (var i = 0; i < tabs.size(); i++) {
                    livestream = client.getLivestreamSync(channels.get(i));
                    circle = tabs.get(i).liveCircle;
                    if (livestream != null && !circle.isVisible()) {
                        circle.setRadius(2);
                        circle.setVisible(true);
                    } else if (livestream == null && circle.isVisible()) {
                        circle.setVisible(false);
                        circle.setRadius(0);
                    }
                }
                log.info("looking for live channels");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException | IOException e) {
                log.severe("could not sleep live eventListener; " + e.getMessage());
            }
        });
    }
}
