package com.digiunion.gui.skin;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.FlowPane;
import lombok.val;

public class RemoveButtonSkin extends ButtonSkin {
    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public RemoveButtonSkin(Button parent ,Button control, FlowPane flow) {
        super(control);
        control.setVisible(false);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        control.setEffect(colorAdjust);
        parent.hoverProperty().addListener(event -> control.setVisible(parent.hoverProperty().get() && !control.isVisible()));
        control.hoverProperty().addListener(event -> {
            if(control.hoverProperty().get())
                colorAdjust.setBrightness(0.25);
            else
                colorAdjust.setBrightness(0);
        });
    }
}
