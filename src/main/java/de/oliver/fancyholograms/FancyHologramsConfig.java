package de.oliver.fancyholograms;

import de.oliver.fancylib.ConfigHelper;
import org.jetbrains.annotations.NotNull;

/**
 * The FancyHologramsConfig class is responsible for managing the configuration of the FancyHolograms plugin.
 * It handles loading and saving hologram data, as well as providing access to various configuration settings.
 */
public final class FancyHologramsConfig {

    @NotNull
    private final FancyHolograms plugin;

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


    FancyHologramsConfig(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the configuration by reloading the plugin's config file and updating the configuration values.
     */
    public void reload() {
        this.plugin.reloadConfig();

        final var config = this.plugin.getConfig();

        versionNotifsMuted = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        autosaveEnabled = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autosaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        defaultVisibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

        this.plugin.saveConfig();
    }


    /**
     * Returns whether version notifications are muted.
     *
     * @return {@code true} if version notifications are muted, {@code false} otherwise.
     */
    public boolean areVersionNotificationsMuted() {
        return this.versionNotifsMuted;
    }

    /**
     * Returns whether autosave is enabled.
     *
     * @return {@code true} if autosave is enabled, {@code false} otherwise.
     */
    public boolean isAutosaveEnabled() {
        return this.autosaveEnabled;
    }

    /**
     * Returns the interval at which autosave is performed.
     *
     * @return The autosave interval in minutes.
     */
    public int getAutosaveInterval() {
        return this.autosaveInterval;
    }

    /**
     * Returns the default visibility distance for holograms.
     *
     * @return The default hologram visibility distance.
     */
    public int getVisibilityDistance() {
        return this.defaultVisibilityDistance;
    }
}
