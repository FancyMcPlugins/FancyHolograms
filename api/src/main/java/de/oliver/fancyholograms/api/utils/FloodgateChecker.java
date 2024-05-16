package de.oliver.fancyholograms.api.utils;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateChecker {

    public static boolean isBedrockPlayer(Player player) {
        if (player == null) {
            return false;
        }

        try {
            boolean b = FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
            System.out.println("FloodgateChecker: " + b);
            return b;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
