package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

import java.util.Locale;

public class LegacyHologramsConfig {

    public static HologramData readHologram(String name, ConfigurationSection config) {
        final var world = config.getString("location.world", "world");
        final var x = config.getDouble("location.x", 0.0);
        final var y = config.getDouble("location.y", 0.0);
        final var z = config.getDouble("location.z", 0.0);
        final var yaw = config.getDouble("location.yaw", 0.0);
        final var pitch = config.getDouble("location.pitch", 0.0);

        final var location = new Location(Bukkit.getWorld(world), x, y, z, ((float) yaw), ((float) pitch));

        final var text = config.getStringList("text");
        final var textHasShadow = config.getBoolean("text_shadow", TextHologramData.DEFAULT_TEXT_SHADOW_STATE);
        final var textUpdateInterval = config.getInt("update_text_interval", TextHologramData.DEFAULT_TEXT_UPDATE_INTERVAL);
        final var visibilityDistance = config.getInt("visibility_distance", DisplayHologramData.DEFAULT_VISIBILITY_DISTANCE);
        final var scaleX = config.getDouble("scale_x", 1);
        final var scaleY = config.getDouble("scale_y", 1);
        final var scaleZ = config.getDouble("scale_z", 1);
        final var shadowRadius = config.getDouble("shadow_radius", DisplayHologramData.DEFAULT_SHADOW_RADIUS);
        final var shadowStrength = config.getDouble("shadow_strength", DisplayHologramData.DEFAULT_SHADOW_STRENGTH);
        final var backgroundName = config.getString("background");
        final var billboardName = config.getString("billboard", DisplayHologramData.DEFAULT_BILLBOARD.name());
        final var textAlignmentName = config.getString("text_alignment", TextHologramData.DEFAULT_TEXT_ALIGNMENT.name());
        final var linkedNpc = config.getString("linkedNpc");

        final var billboard = switch (billboardName.toLowerCase(Locale.ROOT)) {
            case "fixed" -> Display.Billboard.FIXED;
            case "vertical" -> Display.Billboard.VERTICAL;
            case "horizontal" -> Display.Billboard.HORIZONTAL;
            default -> Display.Billboard.CENTER;
        };

        final var textAlignment = switch (textAlignmentName.toLowerCase(Locale.ROOT)) {
            case "right" -> TextDisplay.TextAlignment.RIGHT;
            case "left" -> TextDisplay.TextAlignment.LEFT;
            default -> TextDisplay.TextAlignment.CENTER;
        };

        TextColor background = null;
        if (backgroundName != null) {
            if (backgroundName.equalsIgnoreCase("transparent")) {
                background = Hologram.TRANSPARENT;
            } else if (backgroundName.startsWith("#")) {
                background = TextColor.fromHexString(backgroundName);
            } else {
                background = NamedTextColor.NAMES.value(backgroundName.toLowerCase(Locale.ROOT).trim().replace(' ', '_'));
            }
        }


        DisplayHologramData displayData = new DisplayHologramData(
                location,
                billboard,
                new Vector3f((float) scaleX, (float) scaleY, (float) scaleZ),
                DisplayHologramData.DEFAULT_TRANSLATION,
                null,
                (float) shadowRadius,
                (float) shadowStrength,
                visibilityDistance,
                linkedNpc
        );

        TextHologramData textData = new TextHologramData(
                text,
                background,
                textAlignment,
                textHasShadow,
                textUpdateInterval
        );

        return new HologramData(name, displayData, HologramType.TEXT, textData);
    }


}
