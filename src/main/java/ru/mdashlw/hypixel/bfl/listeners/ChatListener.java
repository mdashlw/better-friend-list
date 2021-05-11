/*
 * MIT License
 *
 * Copyright (c) 2020 mdashlw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.mdashlw.hypixel.bfl.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ChatListener {

  public static final Pattern HEADER_PATTERN = Pattern.compile("(?:<< )?§6Friends \\(Page (\\d+) of (\\d+)\\)(?: >>)?");
  public static final Pattern OFFLINE_FRIEND_PATTERN = Pattern.compile("(?<name>§.\\w+) is currently offline");
  public static final Pattern ONLINE_FRIEND_PATTERN = Pattern
      .compile("(?<name>§.\\w+) is (?:in|watching|playing) (?:an? |the )?(?<game>[^-]+)(?:- (?<mode>[\\w\\s']+))?");
  public static final Pattern IDLE_FRIEND_PATTERN = Pattern.compile("(?<name>§.\\w+) is idle in Limbo");

  @SubscribeEvent
  public void onChatMessageReceived(final ClientChatReceivedEvent event) {
    if (event.type != 0) {
      return;
    }

    final String text = event.message.getUnformattedText();

    if (!text.contains("Friends (Page ")) {
      return;
    }

    event.setCanceled(true);

    final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
    final String[] lines = text.split("\n");

    Matcher match;
    boolean hadHeader = false;

    for (final String line : lines) {
      if (line.isEmpty()) {
        continue;
      }

      if (line.charAt(0) == '-') {
        thePlayer.addChatMessage(new ChatComponentText("§9§m" + line));
      } else if (!hadHeader && (match = ChatListener.HEADER_PATTERN.matcher(line)).find()) {
        hadHeader = true;

        final int currentPage = Integer.parseInt(match.group(1));
        final int maximumPage = Integer.parseInt(match.group(2));

        final IChatComponent leftArrow;
        final IChatComponent rightArrow;

        if (currentPage == 1) {
          leftArrow = new ChatComponentText("§8<<");
        } else {
          final int previousPage = currentPage - 1;

          leftArrow = new ChatComponentText("§9<<")
              .setChatStyle(new ChatStyle()
                  .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                      new ChatComponentText("§7View page " + previousPage)))
                  .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fl " + previousPage)));
        }

        if (currentPage == maximumPage) {
          rightArrow = new ChatComponentText("§8>>");
        } else {
          final int nextPage = currentPage + 1;

          rightArrow = new ChatComponentText("§9>>")
              .setChatStyle(new ChatStyle()
                  .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                      new ChatComponentText("§7View page " + nextPage)))
                  .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fl " + nextPage)));
        }

        final IChatComponent component = new ChatComponentText("                       ")
            .appendSibling(leftArrow)
            .appendText(" §6Friends (" + currentPage + '/' + maximumPage + ") ")
            .appendSibling(rightArrow);

        thePlayer.addChatMessage(component);
      } else if ((match = ChatListener.OFFLINE_FRIEND_PATTERN.matcher(line)).matches()) {
        final String name = match.group("name");
        final IChatComponent component = new ChatComponentText(" §c\u25A0 " + name);

        thePlayer.addChatMessage(component);
      } else if ((match = ChatListener.ONLINE_FRIEND_PATTERN.matcher(line)).matches()) {
        final String name = match.group("name");
        final String game = match.group("game");
        final String mode = match.group("mode");

        final String cleanName = name.substring(2);
        final String formattedGame;

        if (game.startsWith("house ")) {
          formattedGame = "House §b§l" + game.substring(6);
        } else if (game.endsWith("Game")) {
          formattedGame = game.substring(0, game.length() - 5);
        } else if (game.equals("replay")) {
          formattedGame = "Replay";
        } else if (game.equals("unknown realm")) {
          formattedGame = "Unknown Realm";
        } else {
          formattedGame = game.trim();
        }

        final IChatComponent component = new ChatComponentText(" §a\u25A0 ")
            .appendSibling(new ChatComponentText(name)
                .setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                        new ChatComponentText("§7Message " + name)))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/w " + cleanName + ' '))));

        if (mode == null) {
          component.appendText(" §e" + formattedGame);
        } else {
          component.appendText(" §e" + formattedGame + ": " + mode);
        }

        thePlayer.addChatMessage(component);
      } else if ((match = ChatListener.IDLE_FRIEND_PATTERN.matcher(line)).matches()) {
        final String name = match.group("name");
        final String cleanName = name.substring(2);

        final IChatComponent component = new ChatComponentText(" §e\u25A0 ")
            .appendSibling(new ChatComponentText(name)
                .setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                        new ChatComponentText("§7Message " + name)))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/w " + cleanName + ' '))))
            .appendText(" §8Idle");

        thePlayer.addChatMessage(component);
      } else {
        thePlayer.addChatMessage(new ChatComponentText(line));
      }
    }
  }
}
