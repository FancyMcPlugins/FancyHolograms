package de.oliver.fancyholograms.api.events;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramEvent;
import de.oliver.fancyholograms.api.data.HologramData;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a hologram is being updated, the data in the hologram is current and the event holds the new data
 */
public final class HologramUpdateEvent extends HologramEvent {

    private static final HandlerList handlerList = new HandlerList();


    @NotNull
    private final CommandSender player;
    @NotNull
    private final HologramData updatedData;
    @NotNull
    private final HologramModification modification;

    public HologramUpdateEvent(@NotNull final Hologram hologram, @NotNull final CommandSender player, @NotNull final HologramData updatedData, @NotNull final HologramModification modification) {
        super(hologram, false);

        this.player = player;
        this.updatedData = updatedData;
        this.modification = modification;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull CommandSender getPlayer() {
        return this.player;
    }

    /**
     * Returns the current data of the hologram.
     *
     * @return the current data of the hologram
     */
    public @NotNull HologramData getCurrentData() {
        return getHologram().getData();
    }

    /**
     * Returns the updated data of the hologram.
     *
     * @return the updated data of the hologram
     */
    public @NotNull HologramData getUpdatedData() {
        return this.updatedData;
    }

    /**
     * Returns the type of modification performed on the hologram.
     *
     * @return the type of modification performed on the hologram
     */
    public @NotNull HologramModification getModification() {
        return this.modification;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }


    /**
     * Represents the various types of modifications that can be made to a Hologram.
     */
    public enum HologramModification {
        /**
         * @see HologramData#getText()
         */
        TEXT,
        /**
         * @see HologramData#getLocation()
         */
        POSITION,
        /**
         * @see HologramData#getScale()
         */
        SCALE,
        /**
         * @see HologramData#getBillboard()
         */
        BILLBOARD,
        /**
         * @see HologramData#getBackground()
         */
        BACKGROUND,
        /**
         * @see HologramData#isTextHasShadow()
         */
        TEXT_SHADOW,
        /**
         * @see HologramData#getTextAlignment()
         */
        TEXT_ALIGNMENT,
        SEE_THROUGH,
        /**
         * @see HologramData#getShadowRadius()
         */
        SHADOW_RADIUS,
        /**
         * @see HologramData#getShadowStrength()
         */
        SHADOW_STRENGTH,
        /**
         * @see HologramData#getTextUpdateInterval()
         */
        UPDATE_TEXT_INTERVAL,
        /**
         * @see HologramData#getVisibilityDistance()
         */
        UPDATE_VISIBILITY_DISTANCE;
    }

}
