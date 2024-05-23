package de.oliver.fancyholograms.api.data.property.visibility;

import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum Visibility {
    ALL((player, hologram) -> true),
    MANUAL((player, hologram) -> hologram.isShown(player)),
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

    @Nullable
    public static Visibility byString(String value) {
        return Arrays.stream(Visibility.values())
                .filter(visibility -> visibility.toString().equalsIgnoreCase(value))
                .findFirst().orElse(null);
    }

    public interface VisibilityPredicate {

        boolean canSee(Player player, Hologram hologram);
    }
}
