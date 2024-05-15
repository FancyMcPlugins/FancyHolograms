package de.oliver.fancyholograms.api.utils;

import org.bukkit.entity.Player;
import org.geysermc.geyser.api.GeyserApi;

public class GeyserChecker {

    public static boolean isGeyserPlayer(Player player) {
        if (player == null) {
            System.out.println("GeyserChecker: Player is null");
            return false;
        }

        try {
            boolean b = GeyserApi.api().isBedrockPlayer(player.getUniqueId());
            System.out.println("GeyserChecker: " + b);
            return b;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
