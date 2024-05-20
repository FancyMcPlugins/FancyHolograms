package de.oliver.fancyholograms.api.utils;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import org.bukkit.Bukkit;

public class EnabledChecker {

    private static Boolean enabled;
    private static FancyHologramsPlugin plugin;

    public static Boolean isFancyHologramsEnabled() {
        if (enabled == null) {
            enabled = Bukkit.getPluginManager().isPluginEnabled("FancyHolograms");
            if (enabled) {
                try {
                    plugin = (FancyHologramsPlugin) Bukkit.getPluginManager().getPlugin("FancyHolograms");
                } catch (ClassCastException e) {
                    throw new IllegalStateException("API failed to access plugin, if using the FancyHolograms API make sure to set the dependency to compile only.");
                }
            }
        }

        return enabled;
    }

    public static FancyHologramsPlugin getPlugin() {
        return plugin;
    }
}
