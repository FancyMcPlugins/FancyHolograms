package de.oliver.fancyholograms.api.events;

import com.google.common.collect.ImmutableList;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class HologramsUnloadedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableList<Hologram> holograms;

    public HologramsUnloadedEvent(@NotNull final ImmutableList<Hologram> holograms) {
        super(!Bukkit.isPrimaryThread());

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
