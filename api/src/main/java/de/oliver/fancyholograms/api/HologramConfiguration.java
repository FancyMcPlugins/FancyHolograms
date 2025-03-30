package de.oliver.fancyholograms.api;

import org.jetbrains.annotations.NotNull;

public interface HologramConfiguration {

    /**
     * Reloads the configuration.
     *
     * @param plugin The plugin instance.
     */
    void reload(@NotNull FancyHologramsPlugin plugin);

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
     * Returns the log level for the plugin.
     *
     * @return The log level for the plugin.
     */
    String getLogLevel();

    /**
     * Returns whether hologram load logging on world loading is enabled or disabled.
     *
     * @return {@code true} if hologram loading should be logged on world loading, {@code false} otherwise.
     */
    boolean isHologramLoadLogging();

    /**
     * Returns whether version notifications are enabled or disabled.
     *
     * @return {@code true} if version notifications are enabled, {@code false} otherwise.
     */
    boolean areVersionNotificationsEnabled();

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
     * Returns the interval at which hologram visibility is updated.
     *
     * @return The hologram visibility update interval in milliseconds.
     */
    int getUpdateVisibilityInterval();
}
