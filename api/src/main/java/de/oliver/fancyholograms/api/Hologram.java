package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramHideEvent;
import de.oliver.fancyholograms.api.events.HologramShowEvent;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * Abstract base class for creating, updating, and managing holograms.
 * <p>
 * This class provides the basic functionality needed to work with holograms
 * across multiple versions of Minecraft. To create a hologram specific to a version of Minecraft,
 * extend this class and implement the abstract methods.
 * <p>
 * Note that the specific way holograms are created, updated, and deleted
 * will vary depending on the Minecraft version.
 * <p>
 * A Hologram object includes data about the hologram and maintains a set of players to whom the hologram is shown.
 */
public abstract class Hologram {

    public static final int LINE_WIDTH = 1000;
    public static final TextColor TRANSPARENT = () -> 0;
    protected static final int MINIMUM_PROTOCOL_VERSION = 762;


    /**
     * HologramData object containing hologram information.
     */
    @NotNull
    protected final HologramData data;
    /**
     * Set of UUIDs of players to whom the hologram is currently shown.
     */
    @NotNull
    protected final Set<UUID> shown = new HashSet<>();


    protected Hologram(@NotNull final HologramData data) {
        this.data = data;
    }

    /**
     * Checks whether the PlaceholderAPI plugin is enabled.
     *
     * @return true if the PlaceholderAPI plugin is enabled, false otherwise
     */
    private static boolean isUsingPlaceholderApi() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Checks whether the MiniPlaceholders plugin is enabled.
     *
     * @return true if the MiniPlaceholders plugin is enabled, false otherwise
     */
    private static boolean isUsingMiniplaceholders() {
        return Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders");
    }

    /**
     * Returns the HologramData of this Hologram object.
     *
     * @return the HologramData of this Hologram object
     */
    public final @NotNull HologramData getData() {
        return this.data;
    }

    /**
     * Returns the Display entity of this Hologram object.
     * The entity is not registered in the world or server.
     * Only use this method if you know what you're doing.
     *
     * @return the Display entity of this Hologram object
     */
    public abstract @Nullable Display getDisplayEntity();

    /**
     * Abstract method for creating a hologram.
     * This method must be implemented in the version-specific subclass.
     */
    protected abstract void create();

    /**
     * Abstract method for deleting a hologram.
     * This method must be implemented in the version-specific subclass.
     */
    protected abstract void delete();

    /**
     * Abstract method for updating a hologram.
     * This method must be implemented in the version-specific subclass.
     */
    protected abstract void update();

    /**
     * Abstract method for showing a hologram to a specific player.
     * This method must be implemented in the version-specific subclass.
     *
     * @param player the player to whom the hologram should be shown
     * @return true if the hologram was shown successfully, false otherwise
     */
    protected abstract boolean show(@NotNull final Player player);

    /**
     * Abstract method for hiding a hologram from a specific player.
     * This method must be implemented in the version-specific subclass.
     *
     * @param player the player from whom the hologram should be hidden
     * @return true if the hologram was hidden successfully, false otherwise
     */
    protected abstract boolean hide(@NotNull final Player player);

    /**
     * Abstract method for refreshing a hologram for a specific player.
     * This method must be implemented in the version-specific subclass.
     *
     * @param player the player for whom the hologram should be refreshed
     */
    protected abstract void refresh(@NotNull final Player player);

    /**
     * Creates the hologram in the world
     */
    public final void createHologram() {
        create();
    }

    /**
     * Deletes the hologram from the world
     */
    public final void deleteHologram() {
        delete();
    }

    /**
     * Updates the hologram by pushing the data from {@link Hologram#getData()} to the entity
     */
    public final void updateHologram() {
        update();
    }

    /**
     * Shows the hologram to the specified player, firing a HologramShowEvent.
     * If the event is cancelled, or if showing the hologram fails for any reason, this method does nothing.
     *
     * @param player the player to show the hologram to
     * @return true if the hologram was successfully shown to the player, false otherwise
     */
    public final boolean showHologram(@NotNull final Player player) {
        if (!new HologramShowEvent(this, player).callEvent()) {
            return false;
        }

        return show(player);
    }

    /**
     * Hides the hologram from the specified player, firing a HologramHideEvent.
     * If the event is cancelled, or if hiding the hologram fails for any reason, this method does nothing.
     *
     * @param player the player to hide the hologram from
     * @return true if the hologram was successfully hidden from the player, false otherwise
     */
    public final boolean hideHologram(@NotNull final Player player) {
        if (!new HologramHideEvent(this, player).callEvent()) {
            return false;
        }

        return hide(player);
    }

    /**
     * Refreshes the hologram for the specified player by resending its location and entity data
     *
     * @param player the player to refresh the hologram for
     */
    public final void refreshHologram(@NotNull final Player player) {
        refresh(player);
    }

    /**
     * Shows the hologram to a collection of players.
     *
     * @param players the players to show the hologram to
     */
    public final void showHologram(@NotNull final Collection<? extends Player> players) {
        players.forEach(this::showHologram);
    }

    /**
     * Hides the hologram from a collection of players.
     *
     * @param players the players to hide the hologram from
     */
    public final void hideHologram(@NotNull final Collection<? extends Player> players) {
        players.forEach(this::hideHologram);
    }

    /**
     * Refreshes the hologram for a collection of players.
     *
     * @param players the players to refresh the hologram for
     */
    public final void refreshHologram(@NotNull final Collection<? extends Player> players) {
        players.forEach(this::refreshHologram);
    }

    /**
     * Returns a read-only view of the UUIDs of players to whom the hologram is currently shown.
     *
     * @return an read-only set of UUIDs of players to whom the hologram is currently shown
     */
    public final @NotNull @UnmodifiableView Set<UUID> getShownToPlayers() {
        return Collections.unmodifiableSet(this.shown);
    }

    /**
     * Checks whether the hologram is currently shown to a player with a given UUID.
     *
     * @param player the UUID of the player
     * @return true if the hologram is currently shown to the player, false otherwise
     */
    public final boolean isShown(@NotNull final UUID player) {
        return this.shown.contains(player);
    }

    /**
     * Checks whether the hologram is currently shown to a specific player.
     *
     * @param player the player
     * @return true if the hologram is currently shown to the player, false otherwise
     */
    public final boolean isShown(@NotNull final Player player) {
        return isShown(player.getUniqueId());
    }

    /**
     * Checks whether the hologram should be shown to a specific player.
     * This method checks whether the player is in the same world as the hologram and whether the player is within
     * the hologram's visibility distance.
     *
     * @param player the player to check
     * @return true if the hologram should be shown to the player, false otherwise
     */
    protected boolean shouldHologramBeShown(@NotNull final Player player) {
        final var location = getData().getDisplayData().getLocation();
        if (location == null) {
            return false;
        }

        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }

        int visibilityDistance = data.getDisplayData().getVisibilityDistance();
        double distanceSquared = location.distanceSquared(player.getLocation());

        return distanceSquared <= visibilityDistance * visibilityDistance;
    }

    /**
     * Checks and updates the shown state for a player.
     * If the hologram is shown and should not be, it hides it.
     * If the hologram is not shown and should be, it shows it.
     *
     * @param player the player to check and update the shown state for
     */
    public void checkAndUpdateShownStateForPlayer(Player player) {
        boolean isShown = isShown(player);
        boolean shouldHologramBeShown = shouldHologramBeShown(player);

        if (isShown && !shouldHologramBeShown) {
            hideHologram(player);
        } else if (!isShown && shouldHologramBeShown) {
            showHologram(player);
        }
    }

    /**
     * Gets the text shown in the hologram. If a player is specified, placeholders in the text are replaced
     * with their corresponding values for the player.
     *
     * @param player the player to get the placeholders for, or null if no placeholders should be replaced
     * @return the text shown in the hologram
     */
    public final Component getShownText(@Nullable final Player player) {
        if (!(getData().getTypeData() instanceof TextHologramData textData)) {
            return null;
        }

        var text = String.join("\n", textData.getText());

        return ModernChatColorHandler.translate(text, player);
    }

    @Override
    public final boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (!(o instanceof Hologram that)) return false;

        return Objects.equals(this.getData(), that.getData());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getData());
    }

}
