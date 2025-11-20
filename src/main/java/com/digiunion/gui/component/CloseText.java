package com.digiunion.gui.component;

import com.digiunion.gui.GUI;
import com.digiunion.kick.model.Channel;
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
            final String username = tab.text.getText();
            for (int i = 0; i < GUI.channels.size(); i++) {
              var channel = GUI.channels.get(i);
              if(GUI.tabs.get(i).text.getText().equals(username)) {
                GUI.channels.remove(i);
                GUI.tabs.remove(i);
              }
            }
            GUI.flow.getChildren().remove(tab);
            try {
                GUI.database.deleteChannelByUsername(username);
            } catch (SQLException | NullPointerException e) {
                System.err.printf("[\033[31mSEVERE\033[0m] could not delete %s tab; %s\n", username, e.getMessage());
            }
        });
    }
}
