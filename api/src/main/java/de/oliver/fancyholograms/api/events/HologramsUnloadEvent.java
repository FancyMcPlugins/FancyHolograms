package de.oliver.fancyholograms.api.events;

import com.google.common.collect.ImmutableList;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when holograms are unloaded in the system.
 * This event contains the list of holograms that are being unloaded.
 * <p>
 * This event is not cancellable.
 */
public final class HologramsUnloadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableList<Hologram> holograms;

    public HologramsUnloadEvent(@NotNull final ImmutableList<Hologram> holograms) {
        super(!Bukkit.isPrimaryThread());

        this.holograms = holograms;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull ImmutableList<Hologram> getHolograms() {
        return this.holograms;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
