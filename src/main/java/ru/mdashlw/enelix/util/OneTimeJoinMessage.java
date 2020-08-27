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
