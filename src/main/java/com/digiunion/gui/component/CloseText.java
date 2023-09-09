package com.digiunion.gui.component;

import com.digiunion.gui.GUI;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import lombok.val;

import java.sql.SQLException;

@Log
public class CloseText extends Text {
    public CloseText(Tab tab, String text){
        super(text);
        setStyle("""
              -fx-fill: white;
              -fx-font-size: 15px;
              -fx-font-weight: 200;
             """);
//        styleProperty().bind(parent.styleProperty());
        setFocusTraversable(false);
        setVisible(false);
        val colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
        hoverProperty().addListener(hoverEvent -> {
            if(isHover() && (tab.isHover() || tab.isFocused()))
                setFill(Color.WHITE);
            else
                setFill(Color.LIGHTGREY);
        });
        setOnMouseClicked(clickEvent -> {
            val channelSlug = tab.text.getText();
            GUI.channels.remove(channelSlug);
            GUI.tabs.removeIf(tab1 -> tab1.text.getText().equals(channelSlug));
            GUI.flow.getChildren().remove(tab);
            try {
                GUI.database.deleteChannel(channelSlug);
            } catch (SQLException e) {
                log.severe("could not delete %s tab; %s".formatted(channelSlug, e.getMessage()));
            }
        });
    }
}
