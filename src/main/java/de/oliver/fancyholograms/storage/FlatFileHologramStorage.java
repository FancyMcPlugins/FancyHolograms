package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.HologramStorage;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.data.property.visibility.Visibility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FlatFileHologramStorage implements HologramStorage {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final File DEPRECATED_CONFIG_FILE = new File("plugins/FancyHolograms/config.yml");
    private static final File HOLOGRAMS_CONFIG_FILE = new File("plugins/FancyHolograms/holograms.yml");

    @Override
    public void saveBatch(Collection<Hologram> holograms, boolean override) {
        lock.readLock().lock();

        boolean success = false;
        YamlConfiguration config = null;
        try {
            config = YamlConfiguration.loadConfiguration(HOLOGRAMS_CONFIG_FILE);

            if (override) {
                config.set("holograms", null);
            }

            for (final var hologram : holograms) {
                writeHologram(config, hologram);
            }

            success = true;
        } finally {
            lock.readLock().unlock();
            if (success) {
                saveConfig(config);
            }
        }
    }

    @Override
    public void save(Hologram hologram) {
        lock.readLock().lock();

        boolean success = false;
        YamlConfiguration config = null;
        try {
            config = YamlConfiguration.loadConfiguration(HOLOGRAMS_CONFIG_FILE);
            writeHologram(config, hologram);

            success = true;
        } finally {
            lock.readLock().unlock();
            if (success) {
                saveConfig(config);
            }
        }
    }

    @Override
    public void delete(Hologram hologram) {
        lock.readLock().lock();

        boolean success = false;
        YamlConfiguration config = null;
        try {
            config = YamlConfiguration.loadConfiguration(HOLOGRAMS_CONFIG_FILE);
            config.set("holograms." + hologram.getData().getName(), null);

            success = true;
        } finally {
            lock.readLock().unlock();
            if (success) {
                saveConfig(config);
            }
        }
    }

    @Override
    public Collection<Hologram> loadAll() {
        // try to load holograms from config.yml but then remove from there
        final List<Hologram> holograms = new LinkedList<>();

        YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(FlatFileHologramStorage.DEPRECATED_CONFIG_FILE);
        if (pluginConfig.isConfigurationSection("holograms")) {
            holograms.addAll(readHolograms(FlatFileHologramStorage.DEPRECATED_CONFIG_FILE));
            pluginConfig.set("holograms", null);

            try {
                pluginConfig.save(FlatFileHologramStorage.DEPRECATED_CONFIG_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        holograms.addAll(readHolograms(FlatFileHologramStorage.HOLOGRAMS_CONFIG_FILE));

        return holograms;
    }

    private List<Hologram> readHolograms(File configFile) {
        lock.readLock().lock();
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if (!config.isConfigurationSection("holograms")) {
                return new ArrayList<>(0);
            }

            int configVersion = config.getInt("version", 1);

            List<Hologram> holograms = new ArrayList<>();

            ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
            for (String name : hologramsSection.getKeys(false)) {
                ConfigurationSection holoSection = hologramsSection.getConfigurationSection(name);
                if (holoSection == null) {
                    FancyHolograms.get().getLogger().warning("Could not load hologram section in config");
                    continue;
                }

                if (configVersion == 1) {
                    HologramData data = Legacy.readHologram(name, holoSection);
                    Hologram hologram = FancyHolograms.get().getHologramManager().create(data);
                    holograms.add(hologram);
                    continue;
                }

                String typeName = holoSection.getString("type");
                if (typeName == null) {
                    FancyHolograms.get().getLogger().warning("HologramType was not saved");
                    continue;
                }

                HologramType type = HologramType.getByName(typeName);
                if (type == null) {
                    FancyHolograms.get().getLogger().warning("Could not parse HologramType");
                    continue;
                }

                HologramData hologramData;
                switch (type) {
                    case TEXT -> hologramData = new TextHologramData(name, new Location(null, 0, 0, 0));
                    case ITEM -> hologramData = new ItemHologramData(name, new Location(null, 0, 0, 0));
                    case BLOCK -> hologramData = new BlockHologramData(name, new Location(null, 0, 0, 0));
                    case DROPPED_ITEM -> hologramData = new DroppedItemHologramData(name, new Location(null, 0, 0, 0));
                    default -> hologramData = null;
                }

                if (hologramData == null) {
                    continue;
                }

                hologramData.read(holoSection, name);

                Hologram hologram = FancyHolograms.get().getHologramManager().create(hologramData);
                holograms.add(hologram);
            }

            return holograms;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void writeHologram(YamlConfiguration config, Hologram hologram) {
        @NotNull ConfigurationSection section;
        if (!config.isConfigurationSection("holograms")) {
            section = config.createSection("holograms");
        } else {
            section = Objects.requireNonNull(config.getConfigurationSection("holograms"));
        }

        String holoName = hologram.getData().getName();

        ConfigurationSection holoSection = section.getConfigurationSection(holoName);
        if (holoSection == null) {
            holoSection = section.createSection(holoName);
        }

        hologram.getData().write(holoSection, holoName);
    }

    private void saveConfig(YamlConfiguration config) {
        config.set("version", 2);
        config.setInlineComments("version", List.of("DO NOT CHANGE"));

        FancyHolograms.get().getFileStorageExecutor().execute(() -> {
            lock.writeLock().lock();
            try {
                config.save(HOLOGRAMS_CONFIG_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    static class Legacy {
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
            final var isSeeThrough = config.getBoolean("see_through", TextHologramData.DEFAULT_SEE_THROUGH);
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


            final var visibility = Optional.ofNullable(config.getString("visibility"))
                    .flatMap(Visibility::byString)
                    .orElseGet(() -> {
                        final var visibleByDefault = config.getBoolean("visible_by_default", HologramData.DEFAULT_IS_VISIBLE);
                        if (config.contains("visible_by_default")) {
                            config.set("visible_by_default", null);
                        }
                        if (visibleByDefault) {
                            return Visibility.ALL;
                        } else {
                            return Visibility.PERMISSION_REQUIRED;
                        }
                    });

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

            Color background = null;
            if (backgroundName != null) {
                if (backgroundName.equalsIgnoreCase("transparent")) {
                    background = Hologram.TRANSPARENT;
                } else if (backgroundName.startsWith("#")) {
                    background = Color.fromARGB((int)Long.parseLong(backgroundName.substring(1), 16));
                } else {
                    NamedTextColor named = NamedTextColor.NAMES.value(backgroundName.toLowerCase(Locale.ROOT).trim().replace(' ', '_'));
                    background = named == null ? null : Color.fromARGB(named.value());
                }
            }

            TextHologramData textHologramData = new TextHologramData(name, location);
            textHologramData
                .setText(text)
                .setBackground(background)
                .setTextAlignment(textAlignment)
                .setTextShadow(textHasShadow)
                .setSeeThrough(isSeeThrough)
                .setTextUpdateInterval(textUpdateInterval)
                .setScale(new Vector3f((float) scaleX, (float) scaleY, (float) scaleZ))
                .setShadowRadius((float) shadowRadius)
                .setShadowStrength((float) shadowStrength)
                .setBillboard(billboard)
                .setVisibilityDistance(visibilityDistance)
                .setVisibility(visibility)
                .setLinkedNpcName(linkedNpc);

            return textHologramData;
        }
    }
}
