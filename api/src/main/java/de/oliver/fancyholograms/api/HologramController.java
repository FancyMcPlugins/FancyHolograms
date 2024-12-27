package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The controller for holograms, responsible for showing and hiding them to players.
 */
public interface HologramController {

    /**
     * Shows the hologram to the given players, if they should see it, and it is not already shown to them.
     */
    @ApiStatus.Internal
    void showHologramTo(@NotNull final Hologram hologram, @NotNull final Player ...players);

    /**
     * Hides the hologram from the given players, if they should not see it, and it is shown to them.
     */
    @ApiStatus.Internal
    void hideHologramFrom(@NotNull final Hologram hologram, @NotNull final Player ...players);

    /**
     * Returns whether the given player should see the hologram.
     */
    @ApiStatus.Internal
    boolean shouldSeeHologram(@NotNull final Hologram hologram, @NotNull final Player player);

    /**
     * Spawns the hologram to the given players, if they should see it, and it is not already shown to them.
     * Hide the hologram from the players that should not see it.
     */
    void refreshHologram(@NotNull final Hologram hologram, @NotNull final Player ...players);

}
