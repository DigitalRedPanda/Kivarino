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

import static javafx.animation.Interpolator.EASE_BOTH;

public class Tab extends Region {

    private static final double top = 6;
    private static final double bottom = 6.25;
    public static final Insets unhoveredInset = new Insets(top, 3, bottom, 16);
    private static final Insets hoveredInset = new Insets(top, 0, bottom, 8);
    public final Circle liveCircle = new Circle();
    public ColorAdjust colorAdjust = new ColorAdjust();
    public CloseText closeText ;
    public final HBox hBox = new HBox(5);
    public final Text text;
    public static Tab focusedTab;

    public Tab(String channelName) {
        super();
        closeText = new CloseText(this,"×");
        setId("tab");
        hBox.setAlignment(Pos.CENTER);
        text = new Text(channelName);
        text.setFill(Color.WHITE);
        text.setFont(new Font(15));


        hBox.setPadding(unhoveredInset);
        liveCircle.setRadius(0);
        liveCircle.setFill(Color.RED);
        liveCircle.setVisible(false);
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
        hBox.getChildren().addAll(text, liveCircle, closeText);
        getChildren().add(hBox);
        final Timeline timeline1 = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
            new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0.25, EASE_BOTH)));
      
//        val timeline2 = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), EASE_BOTH)),
//            new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, EASE_BOTH)));
        hoverProperty().addListener(hoverEvent -> {
            if(Tab.focusedTab != this) {
                if (isHover()) {
                    timeline1.setRate(1);
                    timeline1.play();
                    hBox.setPadding(hoveredInset);
                    closeText.setVisible(true);
                    closeText.setFont(Font.font(15));
                }
                else {
                    timeline1.setRate(-1);
                    timeline1.play();
                    hBox.setPadding(unhoveredInset);
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
                hBox.setPadding(hoveredInset);
            }  else  {
                setStyle("-fx-background-color: #404446;");
                closeText.setVisible(false);
                colorAdjust.setBrightness(0);
                closeText.setFont(Font.font(0));
                hBox.setPadding(unhoveredInset);
            }
        });
    }
    private void setDraggable(Node node){

    }
}
