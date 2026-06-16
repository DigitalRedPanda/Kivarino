package com.digiunion.kick.util.irc;

import java.util.Map;
import java.util.HashMap;

public record IRCMessage(    
  Map<String, String> tags,
  Prefix prefix,       
  IRCCommand command,     
  String channel,     
  String message) {   
  public enum IRCCommand {
    UNKOWN, PRIVMSG, NOTICE, JOIN, PART, RECONNECT, USERNOTICE, ROOMSTATE, USERSTATE, PING;
  }
  public record Prefix(String nick, String user, String host){}
  public static IRCMessage parse(String msg) {

    var tmp = msg.split(" ", 5);
    if(!msg.isBlank() && !msg.isEmpty()) {

      if(tmp.length == 5) {
        switch(tmp[2]) {
          case "PRIVMSG" -> {
            if(msg.startsWith("@")) {
              var channel = tmp[3].substring(1);
              var ircCommand = tmp[2];
              var tags = parseTags(tmp[0]);
              var prefix = parsePrefix(tmp[1]);

              return new IRCMessage(tags, prefix, IRCCommand.PRIVMSG, channel,  tmp[4].substring(1));
            }
          }
          case "PING" -> {
            return new IRCMessage(null, new Prefix(null, null, tmp[1].substring(1)), IRCCommand.PING ,null, null);
          }
          default -> {
            return new IRCMessage(null, null, IRCCommand.UNKOWN ,null, null);
          }
        }
      } else if(msg.startsWith("P")) {
        return new IRCMessage(null, new Prefix(null, null, tmp[1].substring(1)), IRCCommand.PING ,null, null);
      }
    } else {
      return new IRCMessage(null, null, IRCCommand.UNKOWN ,null, null);
    }

    return new IRCMessage(null, null, IRCCommand.UNKOWN ,null, null);
  }

  public static Prefix parsePrefix(String input) {
    if(input.startsWith(":k")) {
      return new Prefix(null, null, input.substring(1, input.length()));
    }
    else if(input.startsWith(":")) {
      var length = input.length();
      var temp = new String[3];
      var t0 = input.indexOf('!');
      var t1 = input.indexOf('@');
      temp[0] = input.substring(1, t0);
      temp[1] = input.substring(t0+1, input.indexOf('@'));
      temp[2] = input.substring(t1+1, length);
      return new Prefix(temp[0], temp[1], temp[2]);
    } else {
      return new Prefix(null,null,null);
    }
  }

  public static Map<String, String> parseTags(String input) {
    if(input.startsWith("@")) {
      var tongtong = input.substring(1).split(";");
      var temp = new HashMap<String, String>();
      for(String tmp : tongtong) {
        var tmep = tmp.split("=");
        if(tmep.length == 2)
        temp.put(tmep[0], tmep[1]); 
        else 
        temp.put(tmep[0], "");
      }
      return temp;
    } else {
      return new HashMap<String,String>();
    }
  }
}
