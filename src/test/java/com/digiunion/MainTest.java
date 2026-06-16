package com.digiunion;

import org.junit.platform.commons.annotation.Testable;
import org.junit.jupiter.api.Assertions;

import com.digiunion.kick.util.irc.IRCMessage;
import com.digiunion.kick.util.Slugify;
import com.digiunion.kick.util.KivarinoURLs;

/**
 * Unit test for simple Lmao.
 */
@Testable
public class MainTest {
    /**
     * Rigorous Test.
     */
    public void ircParseTest(){
      IRCMessage.parse("@message_id=6e1c54ee-4bc5-4901-b591-2c1be61ee8f6;sender_username=cute_dove;broadcaster_id=1269875;sender_id=67774809;color=#75FD46;is_anonymous=false;is_verified=false;profile_picture=;badges=subscriber/3 :cute-dove!cute-dove@cute-dove.kivarino.xyz PRIVMSG #n3on :MOG");
    }

    public void ircParserPingTest() {
      IRCMessage.parse("PING :" + KivarinoURLs.BASE_URL.url);
  }
    public void slugTest() {
    Assertions.assertEquals("digital-red-panda", Slugify.slugify("Digital_Red_Panda"));
  }
}
