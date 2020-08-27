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

package ru.mdashlw.enelix.updater;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mdashlw.enelix.util.OneTimeJoinMessage;

public final class Updater {

  public static final Logger LOGGER = LogManager.getLogger();

  private final ModInfo modInfo;
  private final String currentVersion;
  private final String changelogUrl;
  private final String downloadUrl;

  public Updater(final ModInfo modInfo, final String currentVersion, final String changelogUrl,
      final String downloadUrl) {
    this.modInfo = modInfo;
    this.currentVersion = currentVersion;
    this.changelogUrl = changelogUrl;
    this.downloadUrl = downloadUrl;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void check() {
    if (this.currentVersion.endsWith("-dev")) {
      Updater.LOGGER.info("{} v{} is a development version", this.modInfo.getName(), this.currentVersion);

      final IChatComponent component = new ChatComponentText(
          "§cWarning: §eYou are using a development version of " + this.modInfo.getName() +
              ".\n§fPlease install stable version unless you know what you are doing.\n")
          .appendSibling(new ChatComponentText("§6>> §9Download Stable Here §6<<")
              .setChatStyle(new ChatStyle()
                  .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                      new ChatComponentText("§7Click here to download!")))
                  .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.downloadUrl))));

      new OneTimeJoinMessage(component).register();
      return;
    }

    final String content;

    try {
      content = IOUtils.toString(new URL(this.changelogUrl));
    } catch (final IOException exception) {
      Updater.LOGGER.error("Failed to check for updates", exception);
      return;
    }

    final String[] lines = content.split("\n");

    if (lines[0].equals(this.currentVersion)) {
      Updater.LOGGER.info("{} is up to date: {}", this.modInfo.getName(), this.currentVersion);
      return;
    }

    final IChatComponent component = new ChatComponentText("§eAn update is available for ")
        .appendSibling(new ChatComponentText(this.modInfo.getName())
            .setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                    new ChatComponentText("§9" + this.modInfo.getName() + " §bv" + this.currentVersion +
                        " §7by " + this.modInfo.getAuthor())))))
        .appendText("§e. \n")
        .appendSibling(new ChatComponentText("§6>> §9Download Here §6<<")
            .setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                    new ChatComponentText("§7Click here to download!")))
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.downloadUrl))));

    final StringBuilder changelogBuilder = new StringBuilder("\n\n§6Changelog:\n");

    for (final String line : lines) {
      if (this.currentVersion.equals(line)) {
        return;
      }

      if (line.charAt(0) == '-') {
        changelogBuilder.append("§7- ").append("§a").append(line.substring(2)).append('\n');
      } else {
        changelogBuilder.append("§8> ").append("§b").append(line).append('\n');
      }
    }

    component.appendText(changelogBuilder.toString());

    new OneTimeJoinMessage(component).register();
  }

  public void checkAsync() {
    ForkJoinPool.commonPool().execute(this::check);
  }

  public ModInfo getModInfo() {
    return this.modInfo;
  }

  public String getCurrentVersion() {
    return this.currentVersion;
  }

  public String getChangelogUrl() {
    return this.changelogUrl;
  }

  public String getDownloadUrl() {
    return this.downloadUrl;
  }

  public static final class Builder {

    private ModInfo modInfo;
    private String currentVersion;
    private String changelogUrl;
    private String downloadUrl;

    public Builder modInfo(final ModInfo modInfo) {
      this.modInfo = modInfo;
      return this;
    }

    public Builder currentVersion(final String currentVersion) {
      this.currentVersion = currentVersion;
      return this;
    }

    public Builder changelogUrl(final String changelogUrl) {
      this.changelogUrl = changelogUrl;
      return this;
    }

    public Builder downloadUrl(final String downloadUrl) {
      this.downloadUrl = downloadUrl;
      return this;
    }

    public Updater build() {
      return new Updater(this.modInfo, this.currentVersion, this.changelogUrl, this.downloadUrl);
    }
  }
}
