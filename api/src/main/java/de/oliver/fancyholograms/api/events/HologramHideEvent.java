package de.oliver.fancyholograms.api.events;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a hologram is being hidden from a player
 */
public final class HologramHideEvent extends HologramEvent {

    private static final HandlerList handlerList = new HandlerList();


    @NotNull
    private final Player player;

    public HologramHideEvent(@NotNull final Hologram hologram, @NotNull final Player player) {
        super(hologram);

        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }


    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
