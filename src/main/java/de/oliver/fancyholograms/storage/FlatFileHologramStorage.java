package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.HologramStorage;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.data.property.Visibility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FlatFileHologramStorage implements HologramStorage {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
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
        return readHolograms(FlatFileHologramStorage.HOLOGRAMS_CONFIG_FILE, null);
    }

    @Override
    public Collection<Hologram> loadAll(String world) {
        return readHolograms(FlatFileHologramStorage.HOLOGRAMS_CONFIG_FILE, world);
    }

    /**
     * @param world The world to load the holograms from. (null for all worlds)
     */
    private List<Hologram> readHolograms(@NotNull File configFile, @Nullable String world) {
        lock.readLock().lock();
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if (!config.isConfigurationSection("holograms")) {
                return new ArrayList<>(0);
            }

            int configVersion = config.getInt("version", 1);
            if (configVersion != 2) {
                FancyHolograms.get().getLogger().warning("Config version is not 2, skipping loading holograms");
                return new ArrayList<>(0);
            }

            List<Hologram> holograms = new ArrayList<>();

            ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
            for (String name : hologramsSection.getKeys(false)) {
                ConfigurationSection holoSection = hologramsSection.getConfigurationSection(name);
                if (holoSection == null) {
                    FancyHolograms.get().getLogger().warning("Could not load hologram section in config");
                    continue;
                }

                if (world != null && !holoSection.getString("location.world").equals(world)) {
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

                DisplayHologramData displayData = null;
                switch (type) {
                    case TEXT -> displayData = new TextHologramData(name, new Location(null, 0, 0, 0));
                    case ITEM -> displayData = new ItemHologramData(name, new Location(null, 0, 0, 0));
                    case BLOCK -> displayData = new BlockHologramData(name, new Location(null, 0, 0, 0));
                }

                if (!displayData.read(holoSection, name)) {
                    FancyHolograms.get().getLogger().warning("Could not read hologram data - skipping hologram");
                    continue;
                }

                Hologram hologram = FancyHolograms.get().getHologramManager().create(displayData);
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
}
