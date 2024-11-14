package de.oliver.fancyholograms.api;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ScheduledExecutorService;

public interface FancyHologramsPlugin {

    static FancyHologramsPlugin get() {
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
     * Sets the configuration of the plugin.
     *
     * @param configuration The new configuration.
     * @param reload        Whether the configuration should be reloaded.
     */
    void setHologramConfiguration(HologramConfiguration configuration, boolean reload);

    /**
     * @return The hologram storage.
     */
    HologramStorage getHologramStorage();

    /**
     * @return The hologram thread
     */
    ScheduledExecutorService getHologramThread();

    /**
     * Sets the hologram storage.
     *
     * @param storage The new hologram storage.
     * @param reload  Whether the current hologram cache should be reloaded.
     */
    void setHologramStorage(HologramStorage storage, boolean reload);

    class EnabledChecker {

        private static Boolean enabled;
        private static FancyHologramsPlugin plugin;

        public static Boolean isFancyHologramsEnabled() {
            if (enabled != null) return enabled;

            Plugin pl = Bukkit.getPluginManager().getPlugin("FancyHolograms");

            if (pl != null && pl.isEnabled()) {
                try {
                    plugin = (FancyHologramsPlugin) pl;
                } catch (ClassCastException e) {
                    throw new IllegalStateException("API failed to access plugin, if using the FancyHolograms API make sure to set the dependency to compile only.");
                }

                enabled = true;
                return true;
            }

            return false;
        }

        public static FancyHologramsPlugin getPlugin() {
            return plugin;
        }
    }
}
