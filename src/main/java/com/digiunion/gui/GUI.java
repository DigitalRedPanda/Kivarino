package com.digiunion.gui;

import com.digiunion.database.Database;
import com.digiunion.gui.component.Tab;
import com.digiunion.gui.skin.AddButtonSkin;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.User;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GUI extends Application {

    public static Scene scene;

    public static final KickClient client = new KickClient();
    public static final Database database = Database.instance;
    public static Image icon;
    public static final FlowPane flow = new FlowPane();
    public static final ArrayList<String> channels;
    public static Stage primaryStage;

    static {
        ArrayList<Channel> channels1;
        try {
            channels1 = database.getAllChannels();
        } catch (SQLException e) {
            System.err.printf("[\033[31mSEVERE\033[0m]could not load channels; %s\n", e.getMessage());
            channels1 = new ArrayList<>();
        }
        channels = new ArrayList<>(channels1.stream().map(channel -> channel.user().name()).toList());
    }

    public static final ArrayList<Tab> tabs = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) {
        icon = new Image("Kivarino.png");
        GUI.primaryStage = primaryStage;
        final StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #36393e;");
        final Button addButton = new Button("+");
        addButton.setId("add-button");
        //this is supposed to be the default setting
//        flow.setAlignment(Pos.TOP_LEFT);
        flow.setHgap(0);
        flow.setVgap(1);
        addButton.setSkin(new AddButtonSkin(addButton, flow));
        if(!channels.isEmpty()) {
            for (final String channel : channels) {
                final Tab tab = new Tab(channel);
                tabs.add(tab);
                flow.getChildren().add(tab);
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
        primaryStage.show();
        primaryStage.setOnCloseRequest(closeEvent -> {
            primaryStage.close();
            System.exit(0);
        });
        client.getExecutor().execute(() -> {
            do try {
                Livestream livestream;
                Circle circle;
                final ArrayList<String> safetyMeasure = channels;
                final ArrayList<Tab> safetyMeasure2 = tabs;
                for (var i = 0; i < safetyMeasure2.size(); i++) {
                    /*System.out.println(*/livestream = client.getLivestream(safetyMeasure.get(i)).get()/*)*/;
                    circle = safetyMeasure2.get(i).liveCircle;
                    System.out.println(safetyMeasure.get(i));
                    if (livestream != null && !circle.isVisible()) {
                        circle.setRadius(2);
                        circle.setVisible(true);
                    } else if (livestream == null && circle.isVisible()) {
                        circle.setVisible(false);
                        circle.setRadius(0);
                    }
                }
                System.out.println("[\033[34mINFO\033[0m]looking for live channels");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException | ExecutionException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not sleep live eventListener; %s\n", e.getMessage());
            }
            while (true);
        });
    }
    public static void main(String[] args) {
      GUI.launch(args);
    }
}
