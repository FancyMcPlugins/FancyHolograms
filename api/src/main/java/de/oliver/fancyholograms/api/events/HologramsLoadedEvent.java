package de.oliver.fancyholograms.api.events;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class HologramsLoadedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final HologramManager manager;

    public HologramsLoadedEvent(@NotNull final HologramManager manager) {
        super(Bukkit.isPrimaryThread());

        this.manager = manager;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull HologramManager getManager() {
        return this.manager;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
