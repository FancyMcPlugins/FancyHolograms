package de.oliver.fancyholograms.api.data.property.visibility;

import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.entity.Player;

public class PermissionRequiredVisibilityPredicate implements VisibilityPredicate {
    @Override
    public boolean canSee(Player player, Hologram hologram) {
        return player.hasPermission("fancyholograms.viewhologram." + hologram.getData().getName());
    }
}
