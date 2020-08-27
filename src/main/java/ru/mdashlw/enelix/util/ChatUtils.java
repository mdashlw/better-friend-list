package ru.mdashlw.enelix.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;

public final class ChatUtils {

  private ChatUtils() {
  }

  public static int getBreakDashCount() {
    final Minecraft mc = Minecraft.getMinecraft();

    return (int) Math.floor((280 * mc.gameSettings.chatWidth + 40) / 320 * (1 / mc.gameSettings.chatScale) * 53) - 3;
  }

  public static void printBreakLine() {
    final int dashCount = getBreakDashCount();
    final String dashes = StringUtils.repeat('-', dashCount);

    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§9§m" + dashes));
  }
}
