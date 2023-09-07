package com.digiunion.gui.skin;

import com.digiunion.gui.GUI;
import com.digiunion.gui.component.Tab;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;
import lombok.val;

import java.sql.SQLException;

@Log
public class RemoveButtonSkin extends  LabelSkin{
    /**
     * Creates a new ButtonSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public ColorAdjust colorAdjust;
    public RemoveButtonSkin(Tab parent, Label control) {
        super(control);
        control.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        control.setFocusTraversable(false);
        colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        control.setEffect(colorAdjust);
        control.setOnMouseClicked(clickEvent -> {
            val channelSlug = parent.text.getText();
            GUI.channels.remove(channelSlug);
            GUI.tabs.removeIf(tab -> tab.text.getText().equals(channelSlug));
            GUI.flow.getChildren().remove(parent);
            try {
                GUI.database.deleteChannel(channelSlug);
            } catch (SQLException e) {
                log.severe("could not delete %s tab; %s".formatted(channelSlug, e.getMessage()));
            }
        });
//        parent.hoverProperty().addListener(hoverEvent -> {
//            control.setVisible(parent.isHover() || parent.isFocused());
//            if(control.isHover()) {
//                control.setFont(new Font(15));
//                colorAdjust.setBrightness(0.50);
//            }
//            else {
//                colorAdjust.setBrightness(0);
//                control.setFont(new Font(0));
//            }
//        });
    }
}
