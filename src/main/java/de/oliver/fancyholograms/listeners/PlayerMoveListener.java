package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.Hologram;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Location loc = event.getTo();

        for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
            if(hologram.getLocation().getWorld() != loc.getWorld()){
                continue;
            }

            CraftPlayer cp = ((CraftPlayer) event.getPlayer());
            ServerPlayer sp = cp.getHandle();

            double distance = loc.distance(hologram.getLocation());
            if(Double.isNaN(distance))
                continue;

            boolean isCurrentlyVisible = hologram.getIsVisibleForPlayer().getOrDefault(cp.getUniqueId(), false);

            int visibilityDistance = FancyHolograms.getInstance().getFancyHologramsConfig().getVisibilityDistance();

            if(distance > visibilityDistance && isCurrentlyVisible){
                hologram.remove(sp);
            } else if(distance < visibilityDistance && !isCurrentlyVisible) {
                hologram.spawn(sp);
            }
        }
    }

}
