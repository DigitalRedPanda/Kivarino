package com.digiunion.gui.skin;

import com.digiunion.database.Database;
import com.digiunion.gui.GUI;
import com.digiunion.gui.component.Tab;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.java.Log;
import lombok.val;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log
public class AddButtonSkin extends ButtonSkin {

    private final FlowPane pane;

    private final Database database = GUI.getDatabase();

    private final KickClient client = GUI.getClient();

    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public AddButtonSkin(Button control, FlowPane pane) {
        super(control);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        control.setEffect(colorAdjust);
        control.setOnMouseEntered(e -> {
            val fadeInTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(100),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.25, Interpolator.EASE_BOTH)
                ));
            fadeInTimeline.play();
        });
        control.setOnMouseExited(e -> {
            val fadeOutTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.EASE_BOTH)
                ));
            fadeOutTimeline.play();
        });
        control.setOnAction(event -> {
            val newChannel = new Stage();
            val channelName = new TextField();
            channelName.setOnAction(event1 -> {
                Optional<Channel> channel;
                try {
                    channel = client.getChannel(channelName.getText()).thenApply(Optional::ofNullable).get();
                } catch (InterruptedException | ExecutionException e){
                    log.severe("could not add %s channel; %s".formatted(channelName.getText(), e.getMessage()));
                    channel = Optional.empty();
                }
                if (channel.isPresent() && database.insertChannel(channel.get())) {
                    val tab = new Tab(channel.get().user().name().toLowerCase());
                    tab.setId("tab");
                    pane.getChildren().set(pane.getChildren().size() - 1, tab);
                    pane.getChildren().add(control);
                    newChannel.close();
                } else {
                    val errorLabel = new Text("could not add %s, either they were already added or they don't exist (delulu)".formatted(channelName.getText(), channelName.getText(), channelName.getText()));
                    errorLabel.setFill(Color.RED);
                    val hbox = new HBox();
                    hbox.setStyle("-fx-background-color: #36393e;");
                    hbox.setAlignment(Pos.CENTER);
                    hbox.getChildren().add(errorLabel);
                    val scene = new Scene(hbox,450, 100);
                    val error = new Stage(StageStyle.UTILITY);
                    error.setTitle("Error");
                    error.setScene(scene);
                    error.show();
                }



            });
            channelName.setPromptText("name");
            newChannel.setResizable(false);
            newChannel.setHeight(200);
            newChannel.setWidth(600);
            newChannel.setTitle("channel");
            newChannel.getIcons().add(GUI.getIcon());
            val pain = new HBox(5);
            pain.setStyle("-fx-background-color: #36393e;");
            val label = new Label("name:");
            label.setTextFill(Color.WHITE);
            label.setStyle("""
                -fx-font-weight: 200;
                -fx-font-size: 15px;
                """);
            pain.getChildren().addAll(label, channelName);
            pain.setAlignment(Pos.CENTER);
            val channelScene = new Scene(pain, 200, 600);
            newChannel.setScene(channelScene);
            newChannel.show();

        });
        this.pane = pane;
//        control.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
