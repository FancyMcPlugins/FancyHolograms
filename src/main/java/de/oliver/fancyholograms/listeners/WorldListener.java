package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.main.FancyHolograms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            FancyHolograms.get().getFancyLogger().info("Loading holograms for world " + event.getWorld().getName());
            FancyHolograms.get().getHologramsManager().loadHolograms(event.getWorld().getName());
        });
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            FancyHolograms.get().getFancyLogger().info("Unloading holograms for world " + event.getWorld().getName());
            FancyHolograms.get().getHologramsManager().unloadHolograms(event.getWorld().getName());
        });
    }

}
