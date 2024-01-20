package de.oliver.fancyholograms.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a base event related to Holograms. This is an abstract class that other event classes related to Holograms should extend.
 * This event is cancellable, which means it can be prevented from being processed by the server.
 */
public abstract class HologramEvent extends Event implements Cancellable {

    @NotNull
    private final Hologram hologram;


    private boolean cancelled;


    protected HologramEvent(@NotNull final Hologram hologram, final boolean isAsync) {
        super(isAsync);
        this.hologram = hologram;
    }


    /**
     * Returns the hologram involved in this event.
     *
     * @return the hologram involved in this event
     */
    public final @NotNull Hologram getHologram() {
        return this.hologram;
    }


    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public final void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

}
