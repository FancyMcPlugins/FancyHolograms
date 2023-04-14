package de.oliver.fancyholograms.events;

import de.oliver.fancyholograms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a {@link Hologram} is being spawned
 */
public class HologramSpawnEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    @NotNull
    private final Hologram hologram;
    @NotNull
    private final Player player;

    public HologramSpawnEvent(@NotNull Hologram hologram, @NotNull Player player) {
        this.hologram = hologram;
        this.player = player;
    }

    /**
     * @return the {@link Hologram} that is being modified
     */
    public @NotNull Hologram getHologram() {
        return hologram;
    }

    /**
     * @return the {@link Player} to whom the hologram is spawned
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
