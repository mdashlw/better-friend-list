package ru.mdashlw.bfl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.mdashlw.bfl.listeners.ChatListener;
import ru.mdashlw.enelix.updater.ModInfo;
import ru.mdashlw.enelix.updater.Updater;

@Mod(modid = "betterfriendlist", name = "Better Friend List", version = BetterFriendList.VERSION, clientSideOnly = true)
public final class BetterFriendList {

  public static final String VERSION = "1.0.0";

  @EventHandler
  public void onPreInit(final FMLPreInitializationEvent event) {
    this.registerListeners();
  }

  public void registerListeners() {
    MinecraftForge.EVENT_BUS.register(new ChatListener());
  }

  @EventHandler
  public void onPostInit(final FMLPostInitializationEvent event) {
    Updater.builder()
        .modInfo(new ModInfo("Better Friend List", "mdashlw"))
        .currentVersion(BetterFriendList.VERSION)
        .changelogUrl("https://raw.githubusercontent.com/mdashlw/better-friend-list/master/changelog")
        .downloadUrl("https://github.com/mdashlw/better-friend-list/releases/latest")
        .build()
        .checkAsync();
  }
}
