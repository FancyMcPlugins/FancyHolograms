package de.oliver.fancyholograms.api.data.property.visibility;

import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.entity.Player;

public interface VisibilityPredicate {

    boolean canSee(Player player, Hologram hologram);
}
