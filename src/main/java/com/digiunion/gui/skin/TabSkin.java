package com.digiunion.gui.skin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.val;


public class TabSkin  extends ButtonSkin {
//    private final Color defaultColor = Color.web("#404446");
//    private final Color endColor = Color.web("#54626F");
    @Getter
    private final Circle liveCircle;
    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc. Events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public TabSkin(Button control) {
        super(control);
        liveCircle = new Circle();
        liveCircle.setFill(Color.RED);
        liveCircle.setRadius(0);
        liveCircle.setVisible(false);
        control.setGraphic(liveCircle);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.0);
        control.setEffect(colorAdjust);
        control.setOnMouseEntered(e -> {
            if(!control.focusedProperty().get()){
               val fadeInTimeline = new Timeline(
                   new KeyFrame(Duration.ZERO,
                       new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                   new KeyFrame(Duration.millis(100),
                       new KeyValue(colorAdjust.brightnessProperty(), 0.25, Interpolator.EASE_BOTH)
                   ));
               fadeInTimeline.play();
            }
        });
        control.setOnMouseExited(e -> {
            if(!control.focusedProperty().get()) {
                val fadeOutTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.EASE_BOTH)
                ));
                fadeOutTimeline.play();
            }
        });
        control.focusedProperty().addListener(event -> {
            if(control.focusedProperty().get()){
                colorAdjust.setBrightness(0.25);
                control.setStyle("-fx-background-color: #0B6623;");
            }
            else {
                colorAdjust.setBrightness(0);
                control.setStyle("-fx-background-color: #404446;");
            }
        });

//        System.out.println(control.getFont().getName());
//        val colorInput = new ColorInput();
//        colorInput.setPaint(defaultColor);
//        control.setEffect(colorInput);
//        control.setOnMouseClicked(event -> {
//            val transition = new Timeline(
//              new KeyFrame(Duration.ZERO, new KeyValue(colorInput.paintProperty(), defaultColor, Interpolator.EASE_BOTH)),
//              new KeyFrame(Duration.millis(100), new KeyValue(colorInput.paintProperty(), endColor, Interpolator.EASE_BOTH))
//            );
//            transition.play();
//        });

//        control.setOnAction();
    }
}
