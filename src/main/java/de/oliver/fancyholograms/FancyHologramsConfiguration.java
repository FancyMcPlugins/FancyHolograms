package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancylib.ConfigHelper;
import org.jetbrains.annotations.NotNull;

/**
 * The FancyHologramsConfig class is responsible for managing the configuration of the FancyHolograms plugin.
 * It handles loading and saving hologram data, as well as providing access to various configuration settings.
 */
public final class FancyHologramsConfiguration implements HologramConfiguration {

    /**
     * Indicates whether version notifications are muted.
     */
    private boolean versionNotifsMuted;
    /**
     * Indicates whether autosave is enabled.
     */
    private boolean autosaveEnabled;
    /**
     * The interval at which autosave is performed.
     */
    private int autosaveInterval;
    /**
     * The default visibility distance for holograms.
     */
    private int defaultVisibilityDistance;

    /**
     * Indicates whether commands should be registered.
     *
     * This is useful for users who want to use the plugin's API only.
     */
    private boolean registerCommands;

    @Override
    public void reload(@NotNull FancyHologramsPlugin plugin) {
        FancyHolograms pluginImpl = (FancyHolograms) plugin;
        pluginImpl.reloadConfig();

        final var config = pluginImpl.getConfig();

        versionNotifsMuted = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        autosaveEnabled = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autosaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        defaultVisibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);
        registerCommands = (boolean) ConfigHelper.getOrDefault(config, "register_commands", true);

        if (pluginImpl.isEnabled()) {
            plugin.getScheduler().runTaskAsynchronously(pluginImpl::saveConfig);
        } else {
            // Can't dispatch task if plugin is disabled
            pluginImpl.saveConfig();
        }
    }


    @Override
    public boolean areVersionNotificationsMuted() {
        return versionNotifsMuted;
    }

    @Override
    public boolean isAutosaveEnabled() {
        return autosaveEnabled;
    }

    @Override
    public int getAutosaveInterval() {
        return autosaveInterval;
    }

    @Override
    public int getVisibilityDistance() {
        return defaultVisibilityDistance;
    }

    @Override
    public boolean isRegisterCommands() {
        return registerCommands;
    }
}
