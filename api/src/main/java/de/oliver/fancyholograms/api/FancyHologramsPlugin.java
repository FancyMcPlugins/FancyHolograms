package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.utils.EnabledChecker;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public interface FancyHologramsPlugin {

    static FancyHologramsPlugin get() {
        if (EnabledChecker.isFancyHologramsEnabled()) {
            return (FancyHologramsPlugin) EnabledChecker.getPlugin();
        }

        throw new NullPointerException("Plugin is not enabled");
    }

    JavaPlugin getPlugin();

    boolean isUsingViaVersion();

    FancyScheduler getScheduler();

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
     * Returns the hologram storage.
     *
     * @return The hologram storage.
     */
    HologramStorage getHologramStorage();

    /**
     * Sets the hologram storage.
     *
     * @param storage The new hologram storage.
     * @param reload  Whether the current hologram cache should be reloaded.
     */
    void setHologramStorage(HologramStorage storage, boolean reload);
}
