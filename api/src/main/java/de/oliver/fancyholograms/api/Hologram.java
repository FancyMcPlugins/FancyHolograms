package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

    protected abstract void create();

    protected abstract void delete();

    protected abstract void update();

    protected abstract boolean show(@NotNull final Player player);

    protected abstract boolean hide(@NotNull final Player player);

    protected abstract void refresh(@NotNull final Player player);

    public final void createHologram() {
        create();
    }

    public final void deleteHologram() {
        delete();
    }

    /**
     * Must be called asynchronously
     */
    public final void showHologram(Player player) {
        show(player);
    }

    /**
     * Must be called asynchronously
     */
    public final void showHologram(Collection<? extends Player> players) {
        players.forEach(this::showHologram);
    }

    /**
     * Must be called asynchronously
     */
    public final void hideHologram(Player player) {
        hide(player);
    }

    /**
     * Must be called asynchronously
     */
    public final void hideHologram(Collection<? extends Player> players) {
        players.forEach(this::hideHologram);
    }

    public final void updateHologram() {
        update();
    }

    /**
     * Refreshes the hologram for the specified player by resending its location and entity data
     *
     * @param player the player to refresh the hologram for
     */
    public final void refreshHologram(@NotNull final Player player) {
        refresh(player);
    }

    public final void refreshHologram(@NotNull final Collection<? extends Player> players) {
        players.forEach(this::refreshHologram);
    }

    public final @NotNull @UnmodifiableView Set<UUID> getShownToPlayers() {
        return Collections.unmodifiableSet(this.shown);
    }

    public final boolean isShown(@NotNull final UUID player) {
        return this.shown.contains(player);
    }

    public final boolean isShown(@NotNull final Player player) {
        return isShown(player.getUniqueId());
    }

    protected boolean shouldHologramBeShown(@NotNull final Player player) {
        final var location = getData().getDisplayData().getLocation();
        if (location == null) {
            return false;
        }

        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }

        if (!getData().getDisplayData().isVisibleByDefault() && !player.hasPermission("fancyholograms.viewhologram." + data.getName())) {
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
        FancyHologramsPlugin.get().getScheduler().runTaskAsynchronously(() -> {
            boolean isShown = isShown(player);
            boolean shouldHologramBeShown = shouldHologramBeShown(player);

            if (isShown && !shouldHologramBeShown) {
                hideHologram(player);
            } else if (!isShown && shouldHologramBeShown) {
                showHologram(player);
            }
        });
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
