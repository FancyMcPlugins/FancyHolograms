package de.oliver.fancyholograms.api.data.property.visibility;

import de.oliver.fancyholograms.api.Hologram;
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
     * Only players from the list {@link Hologram#manualViewer} can see a hologram.
     *
     * @see Hologram#addManualViewer(UUID) to add player
     * @see Hologram#removeManualViewer(UUID)  to remote player
     */
    MANUAL_VIEWER((player, hologram) -> hologram.getManualViewer().contains(player.getUniqueId())),
    /**
     * The player needs permission to see a specific hologram.
     */
    PERMISSION_REQUIRED(
            (player, hologram) -> player.hasPermission("fancyholograms.viewhologram." + hologram.getData().getName())
    );


    private final VisibilityPredicate predicate;


    Visibility(VisibilityPredicate predicate) {
        this.predicate = predicate;
    }


    public boolean canSee(Player player, Hologram hologram) {
        return this.predicate.canSee(player, hologram);
    }


    public static Optional<Visibility> byString(String value) {
        return Arrays.stream(Visibility.values())
                .filter(visibility -> visibility.toString().equalsIgnoreCase(value))
                .findFirst();
    }

    public interface VisibilityPredicate {

        boolean canSee(Player player, Hologram hologram);
    }
}
