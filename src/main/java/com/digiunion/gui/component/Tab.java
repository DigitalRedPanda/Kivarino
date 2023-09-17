package com.digiunion.gui.component;

import com.digiunion.gui.GUI;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    private static final double top = 6d;
    private static final double bottom = 6.25;
    private static final Insets defaultInset = new Insets(top, 3, bottom, 16);
    private static final Insets modifiedInset = new Insets(top, 0, bottom, 8);
    public final Circle liveCircle = new Circle();
    public final HBox hBox = new HBox(5);
    public final Text text;
    public static Tab focusedTab;

    public Tab(String channelName) {
        super();
        setId("tab");
        hBox.setAlignment(Pos.CENTER);
        text = new Text(channelName);
        text.setFill(Color.WHITE);
        text.setFont(new Font(15));


        hBox.setPadding(defaultInset);
        liveCircle.setRadius(0);
        liveCircle.setFill(Color.RED);
        liveCircle.setVisible(false);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
        val closeText = new CloseText(this,"×");
        hBox.getChildren().addAll(text, liveCircle, closeText);
        getChildren().add(hBox);
        val timeline1 = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
            new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0.25, EASE_BOTH)));
//        val timeline2 = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
//            new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, EASE_BOTH)));
        hoverProperty().addListener(hoverEvent -> {
            if(Tab.focusedTab != this) {
                if (isHover()) {
                    timeline1.play();
                    hBox.setPadding(modifiedInset);
                    closeText.setVisible(true);
                    closeText.setFont(Font.font(15));

                }
                else {
                    new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
                        new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, EASE_BOTH))).play();
                    hBox.setPadding(defaultInset);
                    closeText.setFont(Font.font(0));
                    closeText.setVisible(false);
                }
            }
        });
        setOnMousePressed(clickEvent -> {
            if (!isFocused())
                requestFocus();
        });
        focusedProperty().addListener(focusEvent -> {
            if(!GUI.primaryStage.isFocused() || isFocused()) {
                Tab.focusedTab = this;
                setStyle("-fx-background-color: #0B6623;");
                colorAdjust.setBrightness(0.25);
                closeText.setVisible(true);
                closeText.setFont(Font.font(15));
                hBox.setPadding(modifiedInset);
            } else {
                setStyle("-fx-background-color: #404446;");
                closeText.setVisible(false);
                colorAdjust.setBrightness(0);
                closeText.setFont(Font.font(0));
                hBox.setPadding(defaultInset);
            }
        });
    }
    private void setDraggable(Node node){

    }
}
