package com.digiunion.gui.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.val;

import static javafx.animation.Interpolator.EASE_BOTH;

public class Tab extends Region {
    public final Circle liveCircle = new Circle();
    public final HBox hBox = new HBox(5);
    public final Text text;
    public Tab(String channelName) {
        super();
        setId("tab");
        hBox.setAlignment(Pos.CENTER);
        text = new Text(channelName);
        text.setFill(Color.WHITE);
        text.setFont(new Font(15));
        hBox.setPadding(new Insets(6, 0,6.25,8));
        liveCircle.setRadius(0);
        liveCircle.setFill(Color.RED);
        liveCircle.setVisible(false);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
//        val closeButton = new Button("×");
//        val closeSkin = new RemoveButtonSkin(this, closeButton);
//        closeButton.setSkin(closeSkin);
        val closeText = new CloseText(this,"×");
        hBox.getChildren().addAll(text, liveCircle, closeText);
        getChildren().add(hBox);
        hoverProperty().addListener(hoverEvent -> {
            if(!isFocused()) {
                if (isHover()) {
                    new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
                            new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0.25, EASE_BOTH))).play();
                    closeText.setVisible(true);
                }
                else {
                    new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
                        new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, EASE_BOTH))).play();
                    closeText.setVisible(false);
                }
            }
        });
        setOnMousePressed(clickEvent -> {
            if (!isFocused())
                requestFocus();
        });
        focusedProperty().addListener(focusEvent -> {
            closeText.setVisible(isFocused());
            if(!isFocused()) {
                setStyle("-fx-background-color: #404446;");
                colorAdjust.setBrightness(0);
            } else {
                setStyle("-fx-background-color: #0B6623;");
                colorAdjust.setBrightness(0.25);
            }

        });
    }
}
