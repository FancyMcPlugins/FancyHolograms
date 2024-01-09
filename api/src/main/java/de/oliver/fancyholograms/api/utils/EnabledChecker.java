package de.oliver.fancyholograms.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EnabledChecker {

    private static Boolean isEnabled;
    private static Plugin plugin;

    public static Boolean isFancyHologramsEnabled() {
        if (isEnabled == null) {
            isEnabled = Bukkit.getPluginManager().isPluginEnabled("FancyHolograms");
            if (isEnabled) {
                plugin = Bukkit.getPluginManager().getPlugin("FancyHolograms");
            }
        }

        return isEnabled;
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
