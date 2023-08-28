package com.digiunion.gui.component;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;
import lombok.val;

public class Tab extends Button {
    public Tab(String text) {
        super(text);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
        setOnMouseEntered(e -> {
            if(!focusedProperty().get()){
                val fadeInTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.millis(100),
                        new KeyValue(colorAdjust.brightnessProperty(), 0.25, Interpolator.EASE_BOTH)
                    ));
                fadeInTimeline.play();
            }
        });
        setOnMouseExited(e -> {
            if(!focusedProperty().get()) {
                val fadeOutTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.millis(100), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.EASE_BOTH)
                    ));
                fadeOutTimeline.play();
            }
        });

    }
}
