package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class HologramsConfig {

    public static final File PLUGIN_CONFIG_FILE = new File("plugins/FancyHolograms/config.yml");
    public static final File HOLOGRAMS_CONFIG_FILE = new File("plugins/FancyHolograms/holograms.yml");

    public List<Hologram> readHolograms(File configFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isConfigurationSection("holograms")) {
            return new ArrayList<>(0);
        }

        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) {
            return new ArrayList<>(0);
        }

        List<Hologram> holograms = new ArrayList<>();

        for (String name : section.getKeys(false)) {
            ConfigurationSection holoSection = section.getConfigurationSection(name);
            if (holoSection == null) {
                continue;
            }

            Hologram holo = FancyHolograms.get().getHologramManager().create(readHologram(name, holoSection));
            holograms.add(holo);
        }


        return holograms;
    }

    private HologramData readHologram(String name, ConfigurationSection config) {
        final var world = config.getString("location.world", "world");
        final var x = config.getDouble("location.x", 0.0);
        final var y = config.getDouble("location.y", 0.0);
        final var z = config.getDouble("location.z", 0.0);
        final var yaw = config.getDouble("location.yaw", 0.0);
        final var pitch = config.getDouble("location.pitch", 0.0);

        final var location = new Location(Bukkit.getWorld(world), x, y, z, ((float) yaw), ((float) pitch));

        final var text = config.getStringList("text");
        final var textHasShadow = config.getBoolean("text_shadow", HologramData.DEFAULT_TEXT_SHADOW_STATE);
        final var textUpdateInterval = config.getInt("update_text_interval", HologramData.DEFAULT_TEXT_UPDATE_INTERVAL);
        final var visibilityDistance = config.getInt("visibility_distance", HologramData.DEFAULT_VISIBILITY_DISTANCE);
        final var scaleX = config.getDouble("scale_x", 1);
        final var scaleY = config.getDouble("scale_y", 1);
        final var scaleZ = config.getDouble("scale_z", 1);
        final var shadowRadius = config.getDouble("shadow_radius", HologramData.DEFAULT_SHADOW_RADIUS);
        final var shadowStrength = config.getDouble("shadow_strength", HologramData.DEFAULT_SHADOW_STRENGTH);
        final var background = config.getString("background");
        final var billboard = config.getString("billboard", HologramData.DEFAULT_BILLBOARD.name());
        final var textAlignment = config.getString("text_alignment", HologramData.DEFAULT_TEXT_ALIGNMENT.name());

        final var data = new HologramData(name);

        data.setLocation(location);
        data.setText(text);
        data.setTextHasShadow(textHasShadow);
        data.setTextUpdateInterval(textUpdateInterval);
        data.setVisibilityDistance(visibilityDistance);
        data.setScale((float) scaleX, (float) scaleY, (float) scaleZ);
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

        data.setTextAlignment(switch (textAlignment.toLowerCase(Locale.ROOT)) {
            case "right" -> TextDisplay.TextAlignment.RIGHT;
            case "left" -> TextDisplay.TextAlignment.LEFT;
            default -> TextDisplay.TextAlignment.CENTER;
        });

        data.setLinkedNpcName(config.getString("linkedNpc"));

        return data;
    }

    public void writeHolograms(File configFile, Collection<Hologram> holograms) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection section;
        if (!config.isConfigurationSection("holograms")) {
            section = config.createSection("holograms");
        } else {
            section = config.getConfigurationSection("holograms");
        }

        for (Hologram hologram : holograms) {
            writeHologram(hologram.getData(), section.isConfigurationSection(hologram.getData().getName())
                    ? section.getConfigurationSection(hologram.getData().getName())
                    : section.createSection(hologram.getData().getName()));
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHologram(HologramData data, ConfigurationSection config) {
        config.set("location.world", data.getLocation().getWorld().getName());
        config.set("location.x", data.getLocation().x());
        config.set("location.y", data.getLocation().y());
        config.set("location.z", data.getLocation().z());
        config.set("location.yaw", data.getLocation().getYaw());
        config.set("location.pitch", data.getLocation().getPitch());

        config.set("text", data.getText());
        config.set("text_shadow", data.isTextHasShadow());
        config.set("text_alignment", data.getTextAlignment().name().toLowerCase(Locale.ROOT));
        config.set("update_text_interval", data.getTextUpdateInterval());
        config.set("visibility_distance", data.getVisibilityDistance());

        config.set("scale_x", data.getScale().x);
        config.set("scale_y", data.getScale().y);
        config.set("scale_z", data.getScale().z);
        config.set("shadow_radius", data.getShadowRadius());
        config.set("shadow_strength", data.getShadowStrength());


        final var billboard = data.getBillboard();
        if (billboard == Display.Billboard.CENTER) {
            config.set("billboard", null);
        } else {
            config.set("billboard", billboard.name().toLowerCase(Locale.ROOT));
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

        config.set("background", color);

        config.set("linkedNpc", data.getLinkedNpcName());
    }

    public void removeHologramFromConfig(File configFile, String hologramName) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isConfigurationSection("holograms")) {
            return;
        }

        config.set("holograms." + hologramName, null);

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
