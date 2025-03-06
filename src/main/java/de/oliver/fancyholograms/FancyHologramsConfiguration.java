package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancylib.ConfigHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
     * Indicates whether the plugin should save holograms when they are changed.
     */
    private boolean saveOnChangedEnabled;

    /**
     * The default visibility distance for holograms.
     */
    private int defaultVisibilityDistance;

    /**
     * Indicates whether commands should be registered.
     * <p>
     * This is useful for users who want to use the plugin's API only.
     */
    private boolean registerCommands;

    /**
     * The log level for the plugin.
     */
    private String logLevel;

    /**
     * The interval at which hologram visibility is updated.
     */
    private int updateVisibilityInterval;

    @Override
    public void reload(@NotNull FancyHologramsPlugin plugin) {
        FancyHolograms pluginImpl = (FancyHolograms) plugin;
        pluginImpl.reloadConfig();

        final var config = pluginImpl.getConfig();

        // saving
        autosaveEnabled = (boolean) ConfigHelper.getOrDefault(config, "saving.autosave.enabled", true);
        config.setInlineComments("saving.autosave.enabled", List.of("Whether autosave is enabled."));

        autosaveInterval = (int) ConfigHelper.getOrDefault(config, "saving.autosave.interval", 15);
        config.setInlineComments("saving.autosave.interval", List.of("The interval at which autosave is performed in minutes."));

        saveOnChangedEnabled = (boolean) ConfigHelper.getOrDefault(config, "saving.save_on_changed", true);
        config.setInlineComments("saving.save_on_changed", List.of("Whether the plugin should save holograms when they are changed."));

        // logging
        logLevel = (String) ConfigHelper.getOrDefault(config, "logging.log_level", "INFO");
        config.setInlineComments("logging.log_level", List.of("The log level for the plugin (DEBUG, INFO, WARN, ERROR)."));

        hologramLoadLogging = (boolean) ConfigHelper.getOrDefault(config, "logging.log_on_world_load", true);
        config.setInlineComments("logging.log_on_world_load", List.of("Whether hologram loading should be logged on world loading. Disable this if you load worlds dynamically to prevent console spam."));

        versionNotifs = (boolean) ConfigHelper.getOrDefault(config, "logging.version_notification", true);
        config.setInlineComments("logging.version_notification", List.of("Whether the plugin should send notifications for new updates."));

        config.set("logging.report_errors_to_sentry", null);
        config.setInlineComments("logging.report_errors_to_sentry", null);

        // options
        defaultVisibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);
        config.setInlineComments("visibility_distance", List.of("The default visibility distance for holograms."));

        registerCommands = (boolean) ConfigHelper.getOrDefault(config, "register_commands", true);
        config.setInlineComments("register_commands", List.of("Whether the plugin should register its commands."));

        config.set("report_errors_to_sentry", null);
        config.setInlineComments("report_errors_to_sentry", null);

        config.setInlineComments("log_level", List.of("The log level for the plugin (DEBUG, INFO, WARN, ERROR)."));
        logLevel = (String) ConfigHelper.getOrDefault(config, "log_level", "INFO");

        updateVisibilityInterval = (int) ConfigHelper.getOrDefault(config, "update_visibility_interval", 20);
        config.setInlineComments("update_visibility_interval", List.of("The interval at which hologram visibility is updated in ticks."));

        if (pluginImpl.isEnabled()) {
            plugin.getHologramThread().submit(pluginImpl::saveConfig);
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
    public boolean isSaveOnChangedEnabled() {
        return saveOnChangedEnabled;
    }

    @Override
    public boolean isHologramLoadLogging() {
        return hologramLoadLogging;
    }

    @Override
    public boolean areVersionNotificationsEnabled() {
        return versionNotifs;
    }

    @Override
    public int getDefaultVisibilityDistance() {
        return defaultVisibilityDistance;
    }

    @Override
    public boolean isRegisterCommands() {
        return registerCommands;
    }

    @Override
    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public int getUpdateVisibilityInterval() {
        return updateVisibilityInterval;
    }
}
