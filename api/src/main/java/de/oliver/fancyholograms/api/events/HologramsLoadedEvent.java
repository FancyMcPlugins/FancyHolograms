package de.oliver.fancyholograms.api.events;

import com.google.common.collect.ImmutableList;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HologramsLoadedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableList<Hologram> holograms;

    public HologramsLoadedEvent(@NotNull final ImmutableList<Hologram> holograms) {
        super(true);

        this.holograms = holograms;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull ImmutableList<Hologram> getManager() {
        return this.holograms;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
