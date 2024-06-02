package de.oliver.fancyholograms.api.hologram;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
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
    public static final Color TRANSPARENT = Color.fromARGB(0);
    protected static final int MINIMUM_PROTOCOL_VERSION = 762;

    protected final @NotNull HologramData data;
    /**
     * Set of UUIDs of players to whom the hologram is currently shown.
     */
    protected final @NotNull Set<UUID> viewers = new HashSet<>();

    protected Hologram(@NotNull final HologramData data) {
        this.data = data;
    }

    @NotNull
    public String getName() {
        return data.getName();
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

    public final void showHologram(Collection<? extends Player> players) {
        players.forEach(this::showHologram);
    }

    public final void showHologram(Player player) {
        viewers.add(player.getUniqueId());
    }

    public final void hideHologram(Collection<? extends Player> players) {
        players.forEach(this::hideHologram);
    }

    public final void hideHologram(Player player) {
        viewers.remove(player.getUniqueId());
    }

    @Deprecated(forRemoval = true)
    public final void updateHologram() {
        queueUpdate();
    }

    /**
     * Queues hologram to update and refresh for players
     */
    public final void queueUpdate() {
        data.setHasChanges(true);
    }

    /**
     * Forces hologram to update and refresh for players
     */
    public final void forceUpdate() {
        update();
    }

    /**
     * Refreshes the hologram for the players it is currently shown to.
     */
    public void refreshForViewers() {
        final var players = getViewers()
                .stream()
                .map(Bukkit::getPlayer)
                .toList();

        refreshHologram(players);
    }

    /**
     * Refreshes the hologram for players in the world associated with the hologram's location.
     */
    public void refreshForViewersInWorld() {
        World world = data.getLocation().getWorld();
        final var players = getViewers()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.getWorld().equals(world))
                .toList();

        refreshHologram(players);
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

    public final @NotNull @UnmodifiableView Set<UUID> getViewers() {
        return Collections.unmodifiableSet(this.viewers);
    }

    public final boolean isViewer(@NotNull final Player player) {
        return isViewer(player.getUniqueId());
    }

    public final boolean isViewer(@NotNull final UUID player) {
        return this.viewers.contains(player);
    }

    protected boolean shouldShowTo(@NotNull final Player player) {
        final var location = getData().getLocation();
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }

        if (!this.getData().getVisibility().canSee(player, this)) {
            return false;
        }

        int visibilityDistance = data.getVisibilityDistance();
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
    public void updateShownStateFor(Player player) {
        boolean isShown = isViewer(player);
        boolean shouldBeShown = shouldShowTo(player);

        if (isShown && !shouldBeShown) {
            showHologram(player);
        } else if (!isShown && shouldBeShown) {
            hideHologram(player);
        }
    }

    /**
     * Checks and forcefully updates the shown state for a player.
     * If the hologram is shown and should not be, it hides it.
     * If the hologram is not shown and should be, it shows it.
     *
     * @param player the player to check and update the shown state for
     */
    public void forceUpdateShownStateFor(Player player) {
        boolean isShown = isViewer(player);
        boolean shouldBeShown = shouldShowTo(player);

        if (isShown && !shouldBeShown) {
            hide(player);
        } else if (!isShown && shouldBeShown) {
            show(player);
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
        if (!(getData() instanceof TextHologramData textData)) {
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
