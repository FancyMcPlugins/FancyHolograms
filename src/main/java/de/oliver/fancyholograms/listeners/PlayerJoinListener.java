package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.Hologram;
import de.oliver.fancylib.MessageHelper;
import net.minecraft.server.level.ServerPlayer;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        CraftPlayer craftPlayer = (CraftPlayer) event.getPlayer();
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
            hologram.spawn(serverPlayer);
        }

        if(!FancyHolograms.getInstance().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyHolograms.admin")){
            new Thread(() -> {
                ComparableVersion newestVersion = FancyHolograms.getInstance().getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyHolograms.getInstance().getDescription().getVersion());
                if(newestVersion.compareTo(currentVersion) > 0){
                    MessageHelper.warning(event.getPlayer(), "You are using an outdated version of the FancyHolograms plugin.</color>");
                    MessageHelper.warning(event.getPlayer(), "Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyHolograms.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>.</color>");
                }
            }).start();
        }
    }

}
