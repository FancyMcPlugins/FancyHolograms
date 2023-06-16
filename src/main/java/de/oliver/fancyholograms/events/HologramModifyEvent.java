package de.oliver.fancyholograms.events;

import de.oliver.fancyholograms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a {@link Hologram} gets modified
 */
public class HologramModifyEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    @NotNull
    private final Hologram hologram;
    @NotNull
    private final Player player;
    @NotNull
    private final HologramModification modification;

    public HologramModifyEvent(@NotNull Hologram hologram, @NotNull Player player, @NotNull HologramModification modification) {
        this.hologram = hologram;
        this.player = player;
        this.modification = modification;
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

    /**
     * @return the modified attribute
     */
    public @NotNull HologramModification getModification() {
        return modification;
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

    public enum HologramModification{
        TEXT,
        POSITION,
        SCALE,
        BILLBOARD,
        BACKGROUND,
        TEXT_SHADOW,
        SHADOW_RADIUS,
        SHADOW_STRENGTH,
        UPDATE_TEXT_INTERVAL,
        ;
    }

}
