package de.oliver.fancyholograms.events;

import de.oliver.fancyholograms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a {@link Hologram} is being removed
 */
public class HologramRemoveEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    @NotNull
    private final Hologram hologram;
    @NotNull
    private final Player player;

    public HologramRemoveEvent(@NotNull Hologram hologram, @NotNull Player player) {
        this.hologram = hologram;
        this.player = player;
    }

    /**
     * @return the {@link Hologram} that is being removed
     */
    public @NotNull Hologram getHologram() {
        return hologram;
    }

    /**
     * @return the {@link Player} who removed the npc
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
