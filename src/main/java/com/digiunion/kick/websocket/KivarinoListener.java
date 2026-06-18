package com.digiunion.kick.websocket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.util.concurrent.ExecutionException;

import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;

import com.digiunion.gui.GUI;

import com.digiunion.kick.util.KivarinoURLs;

import com.digiunion.kick.model.Account;

import com.digiunion.kick.util.irc.IRCMessage;
import com.digiunion.kick.util.irc.IRCMessage.IRCCommand;

public class KivarinoListener implements Listener {


  private final Account acc;

  public KivarinoListener(Account account) {
    this.acc = account;
  }

  @Override
  public void onOpen(WebSocket webSocket) {
    var token = GUI.database.getTokenByName(acc.name());
    try {
      webSocket.sendText(new StringBuilder("PASS oauth:").append(token.get().access_token()), true).exceptionally(e -> {
        System.out.printf("[\033[31mSEVERE\033[0m] couldn't complete IRC OAUTH authentication; %s\n", e.getMessage());
        return webSocket.sendClose(1, "connection error").join();
      }).get();
      webSocket.sendText(new StringBuilder("NICK ").append(acc.name()), true).exceptionally(e -> {
        System.out.printf("[\033[31mSEVERE\033[0m] couldn't complete IRC NICK naming; %s\n", e.getMessage());
        return webSocket.sendClose(1, "connection error").join();
      }).get();
      CompletableFuture<WebSocket>[] completableFutures = new CompletableFuture[GUI.channels.size()];
      for (int i = 0; i < completableFutures.length; i++) {
        completableFutures[i] = webSocket.sendText("JOIN #" + GUI.channels.get(i).slug(),true);
      }
      CompletableFuture.allOf(completableFutures).join();

    } catch(ExecutionException | InterruptedException e) {
      System.out.printf("[\033[31mSEVERE\033[0m] could not establish an IRC connection; %s: %s", e.getCause().toString() ,e.getMessage());
      webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "failed to authenticate");

    } finally {
      webSocket.request(1);
    }
  }

  @Override
  public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
    return CompletableFuture.supplyAsync(() -> IRCMessage.parse(data.toString())).thenAccept(o -> { 
      System.out.printf("[\033[34mINFO\033[0m] message: %s\n", data.toString());
      switch(o.command()) {
        case IRCCommand.PRIVMSG -> {
          GUI.channelsChats.get(o.channel()).addMessage(o);
        }
        case IRCCommand.PING -> {
          System.out.println("[\033[34mINFO\033[0m] ouch, stop poking me");
          webSocket.sendText("PONG :" + o.prefix().host(), true).join();
        }

        default -> {
        }
      }
      webSocket.request(1);
    }).exceptionally(e -> {
      System.out.printf("[\033[31mSEVERE\033[0m] error processing this nonsense; %s\n", e.getMessage());
      e.printStackTrace();
      webSocket.request(1);
      return null;
    });
  }

  @Override
  public CompletionStage<?> onClose(WebSocket webSocket, int status, String reason) {
    return CompletableFuture.runAsync(() -> {
      reconnect(webSocket, status, reason);
    });


  }

  // @Override
  //   public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer buffer) {
  //       return CompletableFuture.completedStage(null);
  //   }

  @Override
  public void onError(WebSocket webSocket, Throwable error) {
    System.out.printf("[\033[31mSEVERE\033[0m] fuck; %s\n", error.getMessage());
    GUI.client.getExecutor().submit(() -> {
      reconnect(webSocket, error);
    });
    webSocket.request(1);
  }
  private void reconnect(WebSocket webSocket, int status, String reason) {
    if(!GUI.client.reconnecting.compareAndSet(false, true)) {
      System.out.printf("[\033[31mSEVERE\033[0m] fuck; %s\n", reason);
      while(true) {
        try {
          Thread.sleep(1000);
          WebSocket newSock;
          newSock = GUI.client.webSocket.updateAndGet(a -> GUI.client.connect(this).join());
          if(!newSock.isInputClosed() && !newSock.isOutputClosed()) {
            break;
          } else {
            newSock.abort();
            continue;
          }
        } catch(InterruptedException e) {
          System.out.printf("[\033[31mSEVERE\033[0m] damn it, I can't wait; %s\n", e.getMessage());
          continue;
        } catch (Exception e) {
          System.out.printf("[\033[31mSEVERE\033[0m] damn it, I can't get a socket ffs; %s\n", e.getMessage());
          continue;
        } finally {
          GUI.client.reconnecting.set(false);
          webSocket.abort();
        }
      }
    }
  }

  private void reconnect(WebSocket webSocket, Throwable throwable) {
    if(!GUI.client.reconnecting.compareAndSet(false, true)) {
      System.out.printf("[\033[31mSEVERE\033[0m] fuck; %s\n", throwable.getMessage());
      while(true) {
        try {
          Thread.sleep(1000);
          WebSocket newSock;
          newSock = GUI.client.webSocket.updateAndGet(a -> GUI.client.connect(this).join());
          if(!newSock.isInputClosed() && !newSock.isOutputClosed()) {
            break;
          } else {
            newSock.abort();
            continue;
          }
        } catch(InterruptedException e) {
          System.out.printf("[\033[31mSEVERE\033[0m] damn it, I can't wait; %s\n", e.getMessage());
          continue;
        } catch (Exception e) {
          System.out.printf("[\033[31mSEVERE\033[0m] damn it, I can't get a socket ffs; %s\n", e.getMessage());
          continue;
        } finally {
          GUI.client.reconnecting.set(false);
          webSocket.abort();
        }
      }
    }
  }
}
