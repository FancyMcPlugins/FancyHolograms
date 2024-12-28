package de.oliver.fancyholograms.api.hologram;

import com.google.common.collect.Sets;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * This class provides core functionalities for managing viewers, spawning, despawning, and updating holograms.
 */
public abstract class Hologram {

    public static final int LINE_WIDTH = 1000;
    public static final Color TRANSPARENT = Color.fromARGB(0);
    protected static final int MINIMUM_PROTOCOL_VERSION = 762;

    protected final @NotNull HologramData data;
    protected final @NotNull Set<UUID> viewers;

    protected Hologram(@NotNull final HologramData data) {
        this.data = data;
        this.viewers = new HashSet<>();
    }

    /**
     * Forcefully spawns the hologram and makes it visible to the specified player.
     *
     * @param player the player to whom the hologram should be shown; must not be null
     */
    @ApiStatus.Internal
    public abstract void spawnTo(@NotNull final Player player);

    /**
     * Forcefully despawns the hologram and makes it invisible to the specified player.
     *
     * @param player the player from whom the hologram should be hidden; must not be null
     */
    @ApiStatus.Internal
    public abstract void despawnFrom(@NotNull final Player player);

    /**
     * Updates the hologram for the specified player.
     *
     * @param player the player for whom the hologram should be updated; must not be null
     */
    @ApiStatus.Internal
    public abstract void updateFor(@NotNull final Player player);


    /**
     * @return a copy of the set of UUIDs of players currently viewing the hologram
     */
    public final @NotNull Set<UUID> getViewers() {
        return Sets.newHashSet(this.viewers);
    }

    @ApiStatus.Internal
    public void setViewers(@NotNull final Set<UUID> viewers) {
        this.viewers.clear();
        this.viewers.addAll(viewers);
    }

    @ApiStatus.Internal
    public void removeViewer(@NotNull final UUID viewer) {
        this.viewers.remove(viewer);
    }

    /**
     * @param player the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final Player player) {
        return isViewer(player.getUniqueId());
    }

    /**
     * @param player the uuid of the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final UUID player) {
        return this.viewers.contains(player);
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

    public final @NotNull HologramData getData() {
        return this.data;
    }
}
