package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancylib.ConfigHelper;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * The FancyHologramsConfig class is responsible for managing the configuration of the FancyHolograms plugin.
 * It handles loading and saving hologram data, as well as providing access to various configuration settings.
 */
public final class FancyHologramsConfig {

    @NotNull
    private final FancyHologramsPlugin plugin;

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
    private int     autosaveInterval;
    /**
     * The visibility distance for holograms.
     */
    private int     visibilityDistance;


    FancyHologramsConfig(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the configuration by reloading the plugin's config file and updating the configuration values.
     */
    public void reload() {
        this.plugin.reloadConfig();

        final var config = this.plugin.getConfig();

        versionNotifsMuted = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        autosaveEnabled    = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autosaveInterval   = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

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
     * Returns the visibility distance for holograms.
     *
     * @return The hologram visibility distance.
     */
    public int getVisibilityDistance() {
        return this.visibilityDistance;
    }


    /**
     * Loads holograms from the plugin's configuration and returns a map of hologram names to their corresponding data.
     *
     * @return A map of hologram names to hologram data.
     */
    public @NotNull @Unmodifiable Map<String, HologramData> loadHolograms() {
        final var config = this.plugin.getConfig();

        final var root = config.getConfigurationSection("holograms");
        if (root == null) {
            return Collections.emptyMap();
        }

        final var holograms = new HashMap<String, HologramData>();

        for (final var name : root.getKeys(false)) {
            final var hologramSection = root.getConfigurationSection(name);
            if (hologramSection != null) {
                holograms.put(name.toLowerCase(Locale.ROOT), loadHologram(name, hologramSection));
            }
        }

        return holograms;
    }

    /**
     * Saves holograms to the plugin's configuration based on the provided hologram data.
     *
     * @param holograms The collection of hologram data to save.
     */
    public void saveHolograms(@NotNull @Unmodifiable final Collection<HologramData> holograms) {
        final var config = this.plugin.getConfig();

        final var root = ofNullable(config.getConfigurationSection("holograms"))
                .orElseGet(() -> config.createSection("holograms"));

        for (final var hologram : holograms) {
            saveHologram(hologram, ofNullable(root.getConfigurationSection(hologram.getName()))
                    .orElseGet(() -> root.createSection(hologram.getName())));
        }

        this.plugin.saveConfig();
    }


    /**
     * Loads a hologram's data from a configuration section.
     *
     * @param name    The name of the hologram.
     * @param section The configuration section containing the hologram's data.
     * @return The loaded HologramData object.
     */
    private @NotNull HologramData loadHologram(@NotNull final String name, @NotNull final ConfigurationSection section) {
        Location location = null;

        try {
            location = section.getLocation("location");
        } catch (final Throwable ignored) {
            // ignored, do nothing
        }

        if (location == null) {
            final var locationSection = section.getConfigurationSection("location");
            if (locationSection != null) {
                final var world = locationSection.getString("world", "world");
                final var x     = locationSection.getDouble("x", 0.0);
                final var y     = locationSection.getDouble("y", 0.0);
                final var z     = locationSection.getDouble("z", 0.0);
                final var yaw   = locationSection.getDouble("yaw", 0.0);
                final var pitch = locationSection.getDouble("pitch", 0.0);

                location = new Location(Bukkit.getWorld(world), x, y, z, ((float) yaw), ((float) pitch));
            }
        }


        final var text               = section.getStringList("text");
        final var textHasShadow      = section.getBoolean("text_shadow", HologramData.DEFAULT_TEXT_SHADOW_STATE);
        final var textUpdateInterval = section.getInt("update_text_interval", HologramData.DEFAULT_TEXT_UPDATE_INTERVAL);

        final var scale          = section.getDouble("scale", HologramData.DEFAULT_SCALE);
        final var shadowRadius   = section.getDouble("shadow_radius", HologramData.DEFAULT_SHADOW_RADIUS);
        final var shadowStrength = section.getDouble("shadow_strength", HologramData.DEFAULT_SHADOW_STRENGTH);

        final var background = section.getString("background");
        final var billboard  = section.getString("billboard", HologramData.DEFAULT_BILLBOARD.name());


        final var data = new HologramData(name);

        data.setLocation(location);

        data.setText(text);
        data.setTextHasShadow(textHasShadow);
        data.setTextUpdateInterval(textUpdateInterval);

        data.setScale((float) scale);
        data.setShadowRadius((float) shadowRadius);
        data.setShadowStrength((float) shadowStrength);

        data.setBillboard(switch (billboard.toLowerCase(Locale.ROOT)) {
            case "fixed" -> Display.Billboard.FIXED;
            case "vertical" -> Display.Billboard.VERTICAL;
            case "horizontal" -> Display.Billboard.HORIZONTAL;
            default -> Display.Billboard.CENTER;
        });

        if (background != null) {
            final TextColor color;

            if (background.equalsIgnoreCase("transparent")) {
                color = Hologram.TRANSPARENT;
            } else if (background.startsWith("#")) {
                color = TextColor.fromHexString(background);
            } else {
                color = NamedTextColor.NAMES.value(background.toLowerCase(Locale.ROOT).trim().replace(' ', '_'));
            }

            data.setBackground(color);
        }

        data.setLinkedNpcName(section.getString("linkedNpc"));

        return data;
    }

    /**
     * Saves a hologram's data to a configuration section.
     *
     * @param data    The hologram data to save.
     * @param section The configuration section to save the hologram's data to.
     */
    private void saveHologram(@NotNull final HologramData data, @NotNull final ConfigurationSection section) {
        final var location = data.getLocation();
        if (location == null) {
            section.set("location", null);
        } else {
            final var locationSection = ofNullable(section.getConfigurationSection("location"))
                    .orElseGet(() -> section.createSection("location"));

            locationSection.set("world", ofNullable(location.getWorld()).map(World::getName).orElse(null));
            locationSection.set("x", location.x());
            locationSection.set("y", location.y());
            locationSection.set("z", location.z());
            locationSection.set("yaw", location.getYaw());
            locationSection.set("pitch", location.getPitch());
        }

        section.set("text", data.getText());
        section.set("text_shadow", data.isTextHasShadow());
        section.set("update_text_interval", data.getTextUpdateInterval());

        section.set("scale", data.getScale());
        section.set("shadow_radius", data.getShadowRadius());
        section.set("shadow_strength", data.getShadowStrength());


        final var billboard = data.getBillboard();
        if (billboard == Display.Billboard.CENTER) {
            section.set("billboard", null);
        } else {
            section.set("billboard", billboard.name().toLowerCase(Locale.ROOT));
        }


        final var background = data.getBackground();

        final String color;

        if (background == null) {
            color = null;
        } else if (background == Hologram.TRANSPARENT) {
            color = "transparent";
        } else if (background instanceof NamedTextColor named) {
            color = named.toString();
        } else {
            color = background.asHexString();
        }

        section.set("background", color);

        section.set("linkedNpc", data.getLinkedNpcName());
    }

}
