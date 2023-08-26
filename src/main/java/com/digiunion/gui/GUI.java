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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.util.concurrent.TimeUnit;

@Log
public class GUI extends Application {

    private Scene scene;
    private final KickClient client = new KickClient();

    private final Database database = Database.getInstance();
    @Override
    public void start(Stage primaryStage) {
        Main.main(null);
        val gridPane = new GridPane();
        gridPane.setStyle("""
            -fx-background-color: #36393e;
            """);
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setVgap(1);
        gridPane.setHgap(1);
        val channels = database.getAllChannels();
        channels.forEach(System.out::println);
        gridPane.widthProperty().addListener(event -> System.out.println(gridPane.widthProperty().get()));
        val futures = channels.stream().map(channel -> toButton(channel.user().name().toLowerCase())).toList();
        for (var i = 0; i < futures.size(); i++)
            gridPane.add(futures.get(i), i, 0);
        System.out.println(gridPane.getWidth());
//        gridPane.addRow(0, channel1.join(), channel2.join(), channel3.join(), channel4.join(), channel5.join());
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

        scene = new Scene(gridPane, 600, 800);
        scene.getStylesheets().add("main.css");
        scene.setFill(Color.valueOf("#36393e"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(new Image("Kivarino.png"));
        primaryStage.setTitle("Kivarino");
        primaryStage.show();
        client.getExecutor().execute(() -> {
            while (true) try {
                for (var i = 0; i < futures.size(); i++) {
                    val skin = (TabSkin) futures.get(i).getSkin();
                    val livestream = client.getLivestreamSync(channels.get(i).slug());
                    if (livestream != null && !skin.getLiveCircle().isVisible()){
                        skin.getLiveCircle().setRadius(2);
                        skin.getLiveCircle().setVisible(true);
                    }
                    else if (livestream == null && skin.getLiveCircle().isVisible()){
                        skin.getLiveCircle().setVisible(false);
                        skin.getLiveCircle().setRadius(0);
                    }
                }
                System.out.println("looking for live channels");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                log.severe("could not sleep live eventListener; %s".formatted(e.getMessage()));
            }
        });
    }

    private Button toButton(String text){
        return new Button(text);
    }
}
