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

package ru.mdashlw.hypixel.bfl.util;

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

public final class Updater {

  private static final Logger LOGGER = LogManager.getLogger();

  private final String name;
  private final String currentVersion;
  private final String changelogUrl;
  private final String downloadUrl;

  public Updater(final String name, final String currentVersion, final String changelogUrl, final String downloadUrl) {
    this.name = name;
    this.currentVersion = currentVersion;
    this.changelogUrl = changelogUrl;
    this.downloadUrl = downloadUrl;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void check() {
    final String content;

    try {
      content = IOUtils.toString(new URL(this.changelogUrl));
    } catch (final IOException exception) {
      Updater.LOGGER.error("Failed to check for updates.", exception);
      return;
    }

    final String[] lines = content.split("\n");

    if (lines[0].equals(this.currentVersion)) {
      Updater.LOGGER.info("{} is up to date: {}", this.name, this.currentVersion);
      return;
    }

    final String latestVersion = lines[0];

    final IChatComponent component = new ChatComponentText(
        "§9----------------------- §6" + this.name + " §9-----------------------\n")
        .appendText("§fA new update is available: §cv" + this.currentVersion + " §f-> §av" + latestVersion + "§f.\n\n")
        .appendSibling(new ChatComponentText("§8>>> §bDownload Here §8<<<")
            .setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT,
                    new ChatComponentText("§7Click here to download the new version!")))
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.downloadUrl))));
    final StringBuilder changelogBuilder = new StringBuilder("\n\n§dChangelog:\n");

    for (final String line : lines) {
      if (line.equals(this.currentVersion)) {
        break;
      }

      if (line.charAt(0) == '-') {
        changelogBuilder.append("§8- ").append("§7").append(line.substring(2)).append('\n');
      } else {
        changelogBuilder.append("§8> ").append("§ev").append(line).append('\n');
      }
    }

    component
        .appendText(changelogBuilder.toString())
        .appendText("\n§9------------------------------------------------------");

    new OneTimeJoinMessage(component).register();
  }

  public void checkAsync() {
    ForkJoinPool.commonPool().execute(this::check);
  }

  public String getName() {
    return this.name;
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

    private String name;
    private String currentVersion;
    private String changelogUrl;
    private String downloadUrl;

    public Builder name(final String name) {
      this.name = name;
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
      return new Updater(this.name, this.currentVersion, this.changelogUrl, this.downloadUrl);
    }
  }
}
