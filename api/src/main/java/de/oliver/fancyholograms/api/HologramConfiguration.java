package de.oliver.fancyholograms.api;

import org.jetbrains.annotations.NotNull;

public interface HologramConfiguration {

    /**
     * Reloads the configuration.
     *
     * @param plugin The plugin instance.
     */
    void reload(@NotNull FancyHolograms plugin);

    /**
     * Returns whether version notifications are muted.
     *
     * @return {@code true} if version notifications are muted, {@code false} otherwise.
     */
    boolean areVersionNotificationsMuted();

    /**
     * Returns whether autosave is enabled.
     *
     * @return {@code true} if autosave is enabled, {@code false} otherwise.
     */
    boolean isAutosaveEnabled();

    /**
     * Returns the interval at which autosave is performed.
     *
     * @return The autosave interval in minutes.
     */
    int getAutosaveInterval();

    /**
     * Returns whether the plugin should save holograms when they are changed.
     *
     * @return {@code true} if the plugin should save holograms when they are changed, {@code false} otherwise.
     */
    boolean isSaveOnChangedEnabled();

    /**
     * Returns the default visibility distance for holograms.
     *
     * @return The default hologram visibility distance.
     */
    int getDefaultVisibilityDistance();

    /**
     * Returns whether the plugin should register its commands.
     *
     * @return {@code true} if the plugin should register its commands, {@code false} otherwise.
     */
    boolean isRegisterCommands();

    /**
     * Returns the log level for the plugin.
     *
     * @return The log level for the plugin.
     */
    String getLogLevel();
}
