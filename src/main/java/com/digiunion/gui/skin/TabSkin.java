package com.digiunion.gui.skin;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
        colorAdjust.setBrightness(0);
        control.setEffect(colorAdjust);
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
        System.out.println(control.getFont().getName());
    }
}
