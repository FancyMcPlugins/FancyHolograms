package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HologramsConfig {

    public static final File PLUGIN_CONFIG_FILE = new File("plugins/FancyHolograms/config.yml");
    public static final File HOLOGRAMS_CONFIG_FILE = new File("plugins/FancyHolograms/holograms.yml");

    public List<Hologram> readHolograms(File configFile) {
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
                HologramData data = LegacyHologramsConfig.readHologram(name, holoSection);
                Hologram hologram = FancyHolograms.get().getHologramManager().create(data);
                holograms.add(hologram);
                continue;
            }

            String typeName = holoSection.getString("type", "TEXT");
            HologramType type = HologramType.getByName(typeName);
            if (type == null) {
                FancyHolograms.get().getLogger().warning("Could not parse HologramType");
                continue;
            }

            DisplayHologramData displayData = new DisplayHologramData();
            displayData.read(holoSection, name);

            Data typeData = null;
            switch (type) {
                case TEXT -> typeData = new TextHologramData();
                case ITEM -> typeData = new ItemHologramData();
                case BLOCK -> typeData = new BlockHologramData();
            }

            typeData.read(holoSection, name);

            HologramData data = new HologramData(name, displayData, type, typeData);

            Hologram hologram = FancyHolograms.get().getHologramManager().create(data);
            holograms.add(hologram);
        }

        return holograms;
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
            String holoName = hologram.getData().getName();

            ConfigurationSection holoSection = section.getConfigurationSection(holoName);
            if (holoSection == null) {
                holoSection = section.createSection(holoName);
            }

            hologram.getData().write(holoSection, holoName);
        }

        config.set("version", 2);
        config.setInlineComments("version", List.of("DO NOT CHANGE"));

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
