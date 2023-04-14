package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.Hologram;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
        CraftPlayer craftPlayer = (CraftPlayer) event.getPlayer();
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
            hologram.spawn(serverPlayer);
        }
    }

}
