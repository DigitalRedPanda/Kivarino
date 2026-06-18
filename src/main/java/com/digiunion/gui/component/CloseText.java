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
        if(GUI.tabs.get(i).text.getText().equals(username)) {
          GUI.channels.remove(i);
          var thisTab = GUI.tabs.remove(i);
          GUI.channelsChats.remove(thisTab.text.getText());
          if(i < GUI.tabs.size()) {
            var focusedTab = GUI.tabs.get(i);
            focusedTab.setActive(true);
            Tab.focusedTab = focusedTab;
          } else {
            var focusedTab = GUI.tabs.get(i > 0 ? i-1: i);
            Tab.focusedTab = focusedTab;
            focusedTab.setActive(true);
          }

          break;
        }
      }
      try {
        GUI.database.deleteChannelBySlug(username);
        GUI.flow.getChildren().remove(tab);
        GUI.client.getExecutor().submit(() -> GUI.client.webSocket.get().sendText("PART #" + username, true).join());
      } catch (SQLException | NullPointerException e) {
        System.err.printf("[\033[31mSEVERE\033[0m] could not delete %s tab; %s\n", username, e.getMessage());
      }
    });
    }
}
