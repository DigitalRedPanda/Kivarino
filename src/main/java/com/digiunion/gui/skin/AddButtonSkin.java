package com.digiunion.gui.skin;

import com.digiunion.database.Database;
import com.digiunion.gui.GUI;
import com.digiunion.gui.component.CloseText;
import com.digiunion.gui.component.Tab;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.model.Channel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Optional;

public class AddButtonSkin extends ButtonSkin {

    private final Database database = GUI.database;
    private final KickClient client = GUI.client;

    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     * @param pane pane to add {@link Button tabs} into.
     */
    public AddButtonSkin(Button control, FlowPane pane) {
        super(control);
        control.setFocusTraversable(false);
        final ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        control.setEffect(colorAdjust);
        control.hoverProperty().addListener(hoverEvent -> {
            if(control.isHover())
                new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0.25, Interpolator.EASE_BOTH))).play();
            else
                new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.EASE_BOTH))).play();
        });
        control.setOnAction(event -> {
            final Stage newChannel = new Stage();
            final TextField channelName = new TextField();
            channelName.setOnAction(event1 -> {
//                try {
                    Platform.runLater(() -> {
                        final Optional<Channel> option = client.getChannel(channelName.getText()).thenApply(Optional::ofNullable).join();
                        if(option.isPresent()) {
                            final Channel channelValue = option.get();
                            System.out.printf("[\033[34mINFO\033[0m] channelName: %s, response: %s\n", channelName.getText(), option.get());
                            if(channelValue.slug().equals("damnbaldguy")) {
                                error("this mf doesn't like beethoven").show();
                            }
                            else {
                                try {
                                    database.insertChannel(channelValue);
                                    final Tab tab = new Tab(channelValue.user().name());
                                    tab.setId("tab");
                                    if(channelValue.livestream() != null) {
                                        final Circle circle  = tab.liveCircle;
                                        circle.setRadius(2);
                                        circle.setVisible(true);
                                    }
                                    final ObservableList<Node>  children = pane.getChildren();
                                    children.set(children.size() - 1, tab);
                                    children.add(control);
                                    GUI.tabs.add(tab);
                                    if(Tab.focusedTab != null) {
                                      Tab.focusedTab.setStyle("-fx-background-color: #404446;");
                                      Tab.focusedTab.closeText.setVisible(false);
                                      Tab.focusedTab.colorAdjust.setBrightness(0);
                                      Tab.focusedTab.closeText.setFont(Font.font(0));
                                      Tab.focusedTab.hBox.setPadding(Tab.unhoveredInset);
                                    }
                                    Tab.focusedTab = tab; 
                                    tab.requestFocus();
                                                                      
                                    GUI.channels.add(channelValue);
                                } catch (SQLException e) {
                                    error("could not add %s, either they were already added or they don't exist (delulu)".formatted(channelName.getText())).show();
                                    System.err.printf("[\033[31mSEVERE\033[0m] could not insert %s; %s\n", channelName.getText() ,e.getMessage());
                                }
                            }
                        } else {
                            error("could not add %s, either they were already added or they don't exist (delulu)".formatted(channelName.getText())).show();
                        }
                    });
//                } catch (InterruptedException | ExecutionException e){
//                    log.severe("could not add %s channel; %s".formatted(channelName.getText(), e.getMessage()));
//                }
                newChannel.close();
            });
            channelName.setPromptText("name");
            newChannel.setResizable(false);
            newChannel.setHeight(200);
            newChannel.setWidth(600);
            newChannel.setTitle("channel");
            newChannel.getIcons().add(GUI.icon);
            final HBox pain = new HBox(5);
            pain.setStyle("-fx-background-color: #36393e;");
            final Label label = new Label("name:");
            label.setTextFill(Color.WHITE);
            label.setStyle("""
                -fx-font-weight: 200;
                -fx-font-size: 15px;
                """);
            pain.getChildren().addAll(label, channelName);
            pain.setAlignment(Pos.CENTER);
            final Scene channelScene = new Scene(pain, 200, 600);
            newChannel.setScene(channelScene);
            newChannel.show();

        });
    }

    /**
     *
     * @param message error message tha appears on the {@link Stage stage}
     * @return Stage - the stage that contains default configs
     */
    private Stage error(String message) {
        final Text errorLabel = new Text(message);
        errorLabel.setFill(Color.RED);
        final StackPane painAndAgony = new StackPane();
        painAndAgony.setStyle("-fx-background-color: #36393e;");
        painAndAgony.setAlignment(Pos.CENTER);
        painAndAgony.getChildren().add(errorLabel);
        final Scene scene = new Scene(painAndAgony,500, 100);
        final Stage error = new Stage(StageStyle.UTILITY);
        error.setTitle("Error");
        error.setScene(scene);
        return error;
    }
}
