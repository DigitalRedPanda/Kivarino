package com.digiunion.gui.component;

import com.digiunion.gui.GUI;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class CloseText extends Text {
    public CloseText(Tab tab, String text){
        super(text);
        setStyle("""
              -fx-fill: #D1CBC1;
              -fx-font-weight: 200;
             """);
        setFont(Font.font(0));
//        styleProperty().bind(parent.styleProperty());
        setFocusTraversable(false);
        final ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        setEffect(colorAdjust);
        hoverProperty().addListener(hoverEvent -> {
//            System.out.println(isHover());
            if(isHover()) {
                setFill(Color.WHITE);
            }
            else
                setFill(Color.web("#D1CBC1"));
        });
        setOnMouseClicked(clickEvent -> {
            final String channelSlug = tab.text.getText();
            GUI.channels.remove(channelSlug);
            GUI.tabs.removeIf(tab1 -> tab1.text.getText().equals(channelSlug));
            GUI.flow.getChildren().remove(tab);
            try {
                GUI.database.deleteChannel(channelSlug);
            } catch (SQLException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not delete %s tab; %s\n", channelSlug, e.getMessage());
            }
        });
    }
}
