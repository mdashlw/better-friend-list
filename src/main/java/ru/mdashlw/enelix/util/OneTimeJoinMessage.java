package ru.mdashlw.enelix.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class OneTimeJoinMessage {

  private final IChatComponent component;
  private final boolean breakLines;

  public OneTimeJoinMessage(final IChatComponent component) {
    this(component, true);
  }

  public OneTimeJoinMessage(final IChatComponent component, final boolean breakLines) {
    this.component = component;
    this.breakLines = breakLines;
  }

  public void register() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onJoin(final EntityJoinWorldEvent event) {
    if (Minecraft.getMinecraft().isSingleplayer()) {
      return;
    }

    if (!(event.entity instanceof EntityPlayerSP)) {
      return;
    }

    if (this.breakLines) {
      ChatUtils.printBreakLine();
    }

    event.entity.addChatMessage(this.component);

    if (this.breakLines) {
      ChatUtils.printBreakLine();
    }

    MinecraftForge.EVENT_BUS.unregister(this);
  }

  public IChatComponent getComponent() {
    return this.component;
  }

  public boolean isBreakLines() {
    return this.breakLines;
  }
}
