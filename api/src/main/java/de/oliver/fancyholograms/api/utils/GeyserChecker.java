package de.oliver.fancyholograms.api.utils;

import org.bukkit.entity.Player;
import org.geysermc.geyser.api.GeyserApi;

public class GeyserChecker {

    public static boolean isGeyserPlayer(Player player) {
        if (player == null) return false;

        try {
            return GeyserApi.api().connectionByUuid(player.getUniqueId()) != null;
        } catch (Throwable e) {
            return false;
        }
    }
}
