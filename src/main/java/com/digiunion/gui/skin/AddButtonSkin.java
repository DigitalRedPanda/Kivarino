package com.digiunion.gui.skin;

import com.digiunion.gui.GUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.val;

public class AddButtonSkin extends ButtonSkin {

    private final FlowPane pane;

    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public AddButtonSkin(Button control, FlowPane pane) {
        super(control);
        control.setOnAction(event -> {
            val newChannel = new Stage();
            val channelName = new TextField();
            channelName.setPromptText("name");
            newChannel.setResizable(false);
            newChannel.setHeight(200);
            newChannel.setWidth(600);
            newChannel.setTitle("channel");
            newChannel.getIcons().add(GUI.getIcon());
            val pain = new StackPane();
            pain.setStyle("-fx-background-color: #36393e;");
            pain.getChildren().add(channelName);
            pain.setAlignment(Pos.CENTER);
            val channelScene = new Scene(pain, 200, 600);
            newChannel.setScene(channelScene);
            newChannel.show();
        });
        this.pane = pane;
        control.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
