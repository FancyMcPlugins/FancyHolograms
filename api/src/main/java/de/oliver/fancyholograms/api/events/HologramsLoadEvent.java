package de.oliver.fancyholograms.api.events;

import com.google.common.collect.ImmutableList;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event triggered when all holograms are loaded.
 * This event contains a list of all holograms that have been loaded in the current context.
 * The event is asynchronous if it does not execute on the main server thread.
 * <p>
 * This event may serve as a notification mechanism to inform listeners that the loading operation
 * for holograms has completed.
 * <p>
 * This event extends the {@link Event} class, utilizing the Bukkit event system.
 */
public final class HologramsLoadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableList<Hologram> holograms;

    public HologramsLoadEvent(@NotNull final ImmutableList<Hologram> holograms) {
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
