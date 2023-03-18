package de.oliver.listeners;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import de.oliver.utils.VersionFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
                ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyHolograms.getInstance().getDescription().getVersion());
                if(newestVersion.compareTo(currentVersion) > 0){
                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] You are using an outdated version of the FancyHolograms plugin.</color>"));
                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>.</color>"));
                }
            }).start();
        }
    }

}
