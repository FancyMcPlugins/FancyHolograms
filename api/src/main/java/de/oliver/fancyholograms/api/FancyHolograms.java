package de.oliver.fancyholograms.api;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

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

    HologramConfiguration getHologramConfiguration();

    @ApiStatus.Internal
    Function<HologramData, Hologram> getHologramFactory();

    ScheduledExecutorService getHologramThread();

    HologramRegistry getRegistry();

    HologramController getController();


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
