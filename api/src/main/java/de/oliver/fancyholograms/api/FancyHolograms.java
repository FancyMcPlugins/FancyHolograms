package de.oliver.fancyholograms.api;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ScheduledExecutorService;

public interface FancyHolograms {

    static FancyHolograms get() {
        if (isEnabled()) {
            return EnabledChecker.getPlugin();
        }

        throw new NullPointerException("Plugin is not enabled");
    }

    static boolean isEnabled() {
        return EnabledChecker.isFancyHologramsEnabled();
    }

    JavaPlugin getPlugin();

    ExtendedFancyLogger getFancyLogger();

    HologramManager getHologramManager();

    /**
     * Returns the configuration of the plugin.
     *
     * @return The configuration.
     */
    HologramConfiguration getHologramConfiguration();

    /**
     * @return The hologram thread
     */
    ScheduledExecutorService getHologramThread();


    class EnabledChecker {

        private static Boolean enabled;
        private static FancyHolograms plugin;

        public static Boolean isFancyHologramsEnabled() {
            if (enabled != null) return enabled;

            Plugin pl = Bukkit.getPluginManager().getPlugin("FancyHolograms");

            if (pl != null && pl.isEnabled()) {
                try {
                    plugin = (FancyHolograms) pl;
                } catch (ClassCastException e) {
                    throw new IllegalStateException("API failed to access plugin, if using the FancyHolograms API make sure to set the dependency to compile only.");
                }

                enabled = true;
                return true;
            }

            return false;
        }

        public static FancyHolograms getPlugin() {
            return plugin;
        }
    }
}
