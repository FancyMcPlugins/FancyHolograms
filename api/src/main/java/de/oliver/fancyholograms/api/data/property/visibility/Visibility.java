package de.oliver.fancyholograms.api.data.property.visibility;

import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Visibility {
    /**
     * Everybody can see a hologram.
     */
    ALL((player, hologram) -> true),
    /**
     * Manual control.
     */
    MANUAL((player, hologram) -> hologram.isShown(player)),
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
