package ru.mdashlw.bfl.listeners;

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
import org.apache.commons.lang3.StringUtils;
import ru.mdashlw.enelix.util.ChatUtils;

public final class ChatListener {

  public static final Pattern HEADER_PATTERN = Pattern.compile("(?:<< )?§6Friends \\(Page (\\d+) of (\\d+)\\)(?: >>)?");
  public static final Pattern OFFLINE_FRIEND_PATTERN = Pattern.compile("(?<name>§.\\w+) is currently offline");
  public static final Pattern ONLINE_FRIEND_PATTERN = Pattern
      .compile("(?<name>§.\\w+) is (?:in|watching) (?:an? |the )?(?<game>[^-\n]+)(?:- (?<mode>[\\w\\s']+))?");
  public static final Pattern IDLE_FRIEND_PATTERN = Pattern.compile("(?<name>§.\\w+) is idle in Limbo");

  @SubscribeEvent
  public void onChatMessageReceived(final ClientChatReceivedEvent event) {
    if (event.type != (byte) 0) {
      return;
    }

    final String text = event.message.getUnformattedText();

    if (!text.contains("Friends (Page ")) {
      return;
    }

    event.setCanceled(true);

    final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    final String[] lines = text.split("\n");

    Matcher match;
    boolean hadHeader = false;

    for (final String line : lines) {
      if (line.isEmpty()) {
        // skip empty lines
      } else if (line.charAt(0) == '-') {
        ChatUtils.printBreakLine();
      } else if (!hadHeader && (match = ChatListener.HEADER_PATTERN.matcher(line)).find()) {
        hadHeader = true;

        final int currentPage = Integer.parseInt(match.group(1));
        final int maximumPage = Integer.parseInt(match.group(2));

        final IChatComponent leftArrow;
        final IChatComponent rightArrow;

        if (currentPage == 1) {
          leftArrow = new ChatComponentText("§8<");
        } else {
          final int previousPage = currentPage - 1;

          leftArrow = new ChatComponentText("§9<")
              .setChatStyle(new ChatStyle()
                  .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                      new ChatComponentText("§7Page " + previousPage)))
                  .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fl " + previousPage)));
        }

        if (currentPage == maximumPage) {
          rightArrow = new ChatComponentText("§8>");
        } else {
          final int nextPage = currentPage + 1;

          rightArrow = new ChatComponentText("§9>")
              .setChatStyle(new ChatStyle()
                  .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                      new ChatComponentText("§7Page " + nextPage)))
                  .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fl " + nextPage)));
        }

        final IChatComponent component = new ChatComponentText(
            StringUtils.repeat(' ', ChatUtils.getBreakDashCount() / 2))
            .appendSibling(leftArrow)
            .appendText(" §6Friends (" + currentPage + '/' + maximumPage + ") ")
            .appendSibling(rightArrow);

        player.addChatMessage(component);
      } else if ((match = ChatListener.OFFLINE_FRIEND_PATTERN.matcher(line)).matches()) {
        final String name = match.group("name");
        final IChatComponent component = new ChatComponentText(" §c\u25A0 " + name);

        player.addChatMessage(component);
      } else if ((match = ChatListener.ONLINE_FRIEND_PATTERN.matcher(line)).matches()) {
        final String name = match.group("name");
        final String game = match.group("game");
        final String mode = match.group("mode");

        final String cleanName = name.substring(2);
        final String formattedGame;

        if (game.endsWith("Game")) {
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

        player.addChatMessage(component);
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

        player.addChatMessage(component);
      } else {
        player.addChatMessage(new ChatComponentText(line));
      }
    }
  }
}