package de.oliver.fancyholograms.api;

import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public interface FancyHologramsPlugin {

    static FancyHologramsPlugin get() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.isPluginEnabled("FancyHolograms")) {
            return (FancyHologramsPlugin) pluginManager.getPlugin("FancyHolograms");
        }

        throw new NullPointerException("Plugin is not enabled");
    }

    JavaPlugin getPlugin();

    boolean isUsingViaVersion();

    FancyScheduler getScheduler();

    HologramManager getHologramManager();

}
