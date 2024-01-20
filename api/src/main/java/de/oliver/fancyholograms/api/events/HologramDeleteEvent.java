package de.oliver.fancyholograms.api.events;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a hologram is being deleted, any hologram data changed will be reflected in the hologram if
 * the event is called
 */
public final class HologramDeleteEvent extends HologramEvent {

    private static final HandlerList handlerList = new HandlerList();


    @NotNull
    private final Player player;

    public HologramDeleteEvent(@NotNull final Hologram hologram, @NotNull final Player player) {
        super(hologram, false);

        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
