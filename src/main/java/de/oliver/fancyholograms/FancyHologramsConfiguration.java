package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The FancyHologramsConfig class is responsible for managing the configuration of the FancyHolograms plugin.
 * It handles loading and saving hologram data, as well as providing access to various configuration settings.
 */
public final class FancyHologramsConfiguration implements HologramConfiguration {

    /**
     * Indicates whether autosave is enabled.
     */
    private boolean autosaveEnabled;
    private static final String CONFIG_AUTOSAVE_ENABLED = "saving.autosave.enabled";

    /**
     * The interval at which autosave is performed.
     */
    private int autosaveInterval;
    private static final String CONFIG_AUTOSAVE_INTERVAL = "saving.autosave.interval";

    /**
     * Indicates whether the plugin should save holograms when they are changed.
     */
    private boolean saveOnChangedEnabled;
    private static final String CONFIG_SAVE_ON_CHANGED = "saving.save_on_changed";

    /**
     * The log level for the plugin.
     */
    private String logLevel;
    private static final String CONFIG_LOG_LEVEL = "logging.log_level";

    /**
     * Indicates whether hologram loading should be logged on world loading.
     */
    private boolean hologramLoadLogging;
    private static final String CONFIG_LOG_ON_WORLD_LOAD = "logging.log_on_world_load";

    /**
     * Indicates whether version notifications are enabled or disabled.
     */
    private boolean versionNotifs;
    private static final String CONFIG_VERSION_NOTIFICATIONS = "logging.version_notifications";

    /**
     * The default visibility distance for holograms.
     */
    private int defaultVisibilityDistance;
    private static final String CONFIG_VISIBILITY_DISTANCE = "visibility_distance";

    /**
     * Indicates whether commands should be registered.
     * <p>
     * This is useful for users who want to use the plugin's API only.
     */
    private boolean registerCommands;
    private static final String CONFIG_REGISTER_COMMANDS = "register_commands";

    /**
     * The interval at which hologram visibility is updated.
     */
    private int updateVisibilityInterval;
    private static final String CONFIG_UPDATE_VISIBILITY_INTERVAL = "update_visibility_interval";

    private static final String CONFIG_REPORT_ERRORS_TO_SENTRY = "report_errors_to_sentry";
    private static final String CONFIG_VERSION = "config_version";

    private static final Map<String, List<String>> CONFIG_COMMENTS = Map.of(
            CONFIG_VERSION, List.of("Config version, do not modify."),
            CONFIG_AUTOSAVE_ENABLED, List.of("Whether autosave is enabled."),
            CONFIG_AUTOSAVE_INTERVAL, List.of("The interval at which autosave is performed in minutes."),
            CONFIG_SAVE_ON_CHANGED, List.of("Whether the plugin should save holograms when they are changed."),
            CONFIG_LOG_LEVEL, List.of("The log level for the plugin (DEBUG, INFO, WARN, ERROR)."),
            CONFIG_LOG_ON_WORLD_LOAD, List.of("Whether hologram loading should be logged on world loading. Disable this if you load worlds dynamically to prevent console spam."),
            CONFIG_VERSION_NOTIFICATIONS, List.of("Whether the plugin should send notifications for new updates."),
            CONFIG_VISIBILITY_DISTANCE, List.of("The default visibility distance for holograms."),
            CONFIG_REGISTER_COMMANDS, List.of("Whether the plugin should register its commands."),
            CONFIG_UPDATE_VISIBILITY_INTERVAL, List.of("The interval at which hologram visibility is updated in ticks.")
    );

    private void updateChecker(@NotNull FancyHolograms plugin, @NotNull FileConfiguration config) {
        final int latestVersion = 1;
        int configVersion = (int) ConfigHelper.getOrDefault(config, CONFIG_VERSION, 0);

        if (configVersion >= latestVersion ) {
            setOptions(config);
            return;
        }
            plugin.getFancyLogger().warn("Outdated config detected! Attempting to migrate previous settings to new config...");

            try {
                var oldConfig = pluginImpl.getConfig();
                File backupFile = new File(pluginImpl.getDataFolder(), "config_old.yml");
                if (backupFile.exists() && !backupFile.canWrite()) {
                    throw new IOException("Unable to backup config to " + backupFile.getPath());
                }
                oldConfig.save(backupFile);

                pluginImpl.saveDefaultConfig();
                var newConfig = pluginImpl.getConfig();

                Map<String, Object> oldConfigValues = oldConfig.getValues(true);
                Map<String, String> keyMap = Map.of(
                    "enable_autosave", CONFIG_AUTOSAVE_ENABLED,
                    "autosave_interval", CONFIG_AUTOSAVE_INTERVAL,
                    "save_on_changed", CONFIG_SAVE_ON_CHANGED,
                    "log_level", CONFIG_LOG_LEVEL,
                    "mute_version_notifications", CONFIG_VERSION_NOTIFICATIONS
                );

                oldConfigValues.forEach((key, value) -> {

                    String newKey = keyMap.getOrDefault(key, null);
                    if (newKey != null) {
                        if (newKey.equals(CONFIG_VERSION_NOTIFICATIONS)) {
                            newConfig.set(newKey, !(Boolean) value);
                        } else {
                            newConfig.set(newKey, value);
                        }
                        plugin.getFancyLogger().info("> CONFIG: Set option '" + key + "' to '" + value + "' from old config.");
                    } else {
                        plugin.getFancyLogger().warn("> CONFIG: Option '" + key + "' is deprecated/invalid! Please migrate this manually from config_old.yml");
                    }
                });

                newConfig.set(CONFIG_VERSION, latestVersion);
                setOptions(newConfig);
                CONFIG_COMMENTS.forEach(config::setInlineComments);

                pluginImpl.getFancyLogger().info("Configuration has finished migrating. Please double check your settings in config.yml.");

            } catch (IOException e) {
                pluginImpl.getFancyLogger().error("Failed to save or reload configuration: " + e.getMessage());
            }
    }

    private void setOptions(@NotNull FileConfiguration config) {

        // saving
        autosaveEnabled = (boolean) ConfigHelper.getOrDefault(config, CONFIG_AUTOSAVE_ENABLED, true);
        autosaveInterval = (int) ConfigHelper.getOrDefault(config, CONFIG_AUTOSAVE_INTERVAL, 15);
        saveOnChangedEnabled = (boolean) ConfigHelper.getOrDefault(config, CONFIG_SAVE_ON_CHANGED, true);
        // logging
        logLevel = (String) ConfigHelper.getOrDefault(config, CONFIG_LOG_LEVEL, "INFO");
        hologramLoadLogging = (boolean) ConfigHelper.getOrDefault(config, CONFIG_LOG_ON_WORLD_LOAD, true);
        versionNotifs = (boolean) ConfigHelper.getOrDefault(config, CONFIG_VERSION_NOTIFICATIONS, true);
        // options
        defaultVisibilityDistance = (int) ConfigHelper.getOrDefault(config, CONFIG_VISIBILITY_DISTANCE, 20);
        registerCommands = (boolean) ConfigHelper.getOrDefault(config, CONFIG_REGISTER_COMMANDS, true);
        updateVisibilityInterval = (int) ConfigHelper.getOrDefault(config, CONFIG_UPDATE_VISIBILITY_INTERVAL, 20);

        config.set(CONFIG_REPORT_ERRORS_TO_SENTRY, null);
    }

    @Override
    public synchronized void reload(@NotNull FancyHologramsPlugin plugin) {
        FancyHolograms pluginImpl = (FancyHolograms) plugin;
        pluginImpl.reloadConfig();

        var config = pluginImpl.getConfig();
        updateChecker(pluginImpl, config);

        if (pluginImpl.isEnabled() && !plugin.getHologramThread().isShutdown()) {
            plugin.getHologramThread().submit(pluginImpl::saveConfig);
        } else {
            // Can't dispatch task if plugin is disabled
            pluginImpl.saveConfig();
        }
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
