package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Collection;
import java.util.List;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        FancyHologramsPlugin.get().getHologramThread().submit(() -> {
            FancyHologramsPlugin.get().getFancyLogger().info("Loading holograms for world " + event.getWorld().getName());

            Collection<HologramData> data = FancyHologramsPlugin.get().getStorage().loadAll(event.getWorld().getName());
            for (HologramData d : data) {
                Hologram hologram = FancyHologramsPlugin.get().getHologramFactory().apply(d);
                FancyHologramsPlugin.get().getRegistry().register(hologram);
            }
        });
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        FancyHologramsPlugin.get().getHologramThread().submit(() -> {
            FancyHologramsPlugin.get().getFancyLogger().info("Unloading holograms for world " + event.getWorld().getName());

            List<Hologram> toUnload = FancyHologramsPlugin.get().getRegistry().getAll().stream()
                    .filter(hologram -> hologram.getData().getLocation().getWorld().equals(event.getWorld()))
                    .toList();

            for (Hologram hologram : toUnload) {
                FancyHologramsPlugin.get().getRegistry().unregister(hologram);
            }
        });
    }

}
