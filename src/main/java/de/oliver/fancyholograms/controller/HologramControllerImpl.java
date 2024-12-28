package de.oliver.fancyholograms.controller;

import de.oliver.fancyholograms.api.HologramController;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class HologramControllerImpl implements HologramController {

    @Override
    public void showHologramTo(@NotNull final Hologram hologram, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = hologram.isViewer(player);
            boolean shouldSee = shouldSeeHologram(hologram, player);

            if (isVisible || !shouldSee) {
                continue;
            }

            hologram.spawnTo(player);
        }
    }

    @Override
    public void hideHologramFrom(@NotNull final Hologram hologram, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = hologram.isViewer(player);
            boolean shouldSee = shouldSeeHologram(hologram, player);

            if (!isVisible || shouldSee) {
                continue;
            }

            hologram.despawnFrom(player);
        }
    }

    @Override
    public boolean shouldSeeHologram(@NotNull final Hologram hologram, @NotNull final Player player) {
        if (!meetsVisibilityConditions(hologram, player)) {
            return false;
        }

        return isWithinVisibilityDistance(hologram, player);
    }

    @Override
    public void refreshHologram(@NotNull final Hologram hologram, @NotNull final Player... players) {
        hideHologramFrom(hologram, players);
        showHologramTo(hologram, players);
    }

    private boolean meetsVisibilityConditions(@NotNull final Hologram hologram, @NotNull final Player player) {
        return hologram.getData().getVisibility().canSee(player, hologram);
    }

    private boolean isWithinVisibilityDistance(@NotNull final Hologram hologram, @NotNull final Player player) {
        final var location = hologram.getData().getLocation();
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }

        int visibilityDistance = hologram.getData().getVisibilityDistance();
        double distanceSquared = location.distanceSquared(player.getLocation());

        return distanceSquared <= visibilityDistance * visibilityDistance;
    }

        /**
     * Syncs a hologram with its linked NPC, if any.
     *
     * @param hologram The hologram to sync.
     */
    public void syncHologramWithNpc(@NotNull final Hologram hologram) {
        final var linkedNpcName = hologram.getData().getLinkedNpcName();
        if (linkedNpcName == null) {
            return;
        }

        final var npc = FancyNpcsPlugin.get().getNpcManager().getNpc(linkedNpcName);
        if (npc == null) {
            return;
        }

        npc.getData().setDisplayName("<empty>");
        npc.getData().setShowInTab(false);
        npc.updateForAll();

        final var npcScale = npc.getData().getScale();

        if(hologram.getData() instanceof DisplayHologramData displayData) {
            displayData.setScale(new Vector3f(npcScale));
        }

        final var location = npc.getData().getLocation().clone().add(0, (npc.getEyeHeight() * npcScale) + (0.5 * npcScale), 0);
        hologram.getData().setLocation(location);
    }
}