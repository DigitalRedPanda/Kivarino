package com.digiunion.gui.skin;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.paint.Color;


public class TabSkin  extends ButtonSkin {

    final Color defaultColor = Color.web("#404446");
    final Color endColor = Color.web("#54626F");
    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc. Events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public TabSkin(Button control) {
        super(control);

    }
}
