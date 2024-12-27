package de.oliver.fancyholograms.api.data.property;

import com.google.common.collect.HashMultimap;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public enum Visibility {
    /**
     * Everybody can see a hologram.
     */
    ALL((player, hologram) -> true),
    /**
     * The player needs permission to see a specific hologram.
     */
    PERMISSION_REQUIRED(
        (player, hologram) -> player.hasPermission("fancyholograms.viewhologram." + hologram.getData().getName())
    ),
    /**
     * The player needs to be added manually through the API
     */
    MANUAL(ManualVisibility::canSee);

    private final VisibilityPredicate predicate;

    Visibility(VisibilityPredicate predicate) {
        this.predicate = predicate;
    }

    public static Optional<Visibility> byString(String value) {
        return Arrays.stream(Visibility.values())
            .filter(visibility -> visibility.toString().equalsIgnoreCase(value))
            .findFirst();
    }

    public boolean canSee(Player player, Hologram hologram) {
        return this.predicate.canSee(player, hologram);
    }

    @FunctionalInterface
    public interface VisibilityPredicate {
        boolean canSee(Player player, Hologram hologram);
    }

    /**
     * Handling of Visibility.MANUAL
     * <br>
     * TODO: Discussion needed - Potentially condense this into one singular multimap within the enum?
     */
    public static class ManualVisibility {
        private static final HashMultimap<String, UUID> distantViewers = HashMultimap.create();

        public static boolean canSee(Player player, Hologram hologram) {
            return hologram.isViewer(player) || distantViewers.containsEntry(hologram.getData().getName(), player.getUniqueId());
        }

        public static void addDistantViewer(Hologram hologram, UUID uuid) {
            addDistantViewer(hologram.getData().getName(), uuid);
        }

        public static void addDistantViewer(String hologramName, UUID uuid) {
            distantViewers.put(hologramName, uuid);
        }

        public static void removeDistantViewer(Hologram hologram, UUID uuid) {
            removeDistantViewer(hologram.getData().getName(), uuid);
        }

        public static void removeDistantViewer(String hologramName, UUID uuid) {
            distantViewers.remove(hologramName, uuid);
        }

        public static void remove(Hologram hologram) {
            remove(hologram.getData().getName());
        }

        public static void remove(String hologramName) {
            distantViewers.removeAll(hologramName);
        }

        public static void clear() {
            distantViewers.clear();
        }
    }
}