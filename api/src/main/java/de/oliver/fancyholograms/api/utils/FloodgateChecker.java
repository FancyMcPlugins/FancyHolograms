package de.oliver.fancyholograms.api.utils;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateChecker {

    public static boolean isBedrockPlayer(Player player) {
        if (player == null) {
            return false;
        }

        try {
            return FloodgateApi.getInstance().isFloodgateId(player.getUniqueId());
        } catch (Throwable ignored) {
            return false;
        }
    }
}
