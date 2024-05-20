package de.oliver.fancyholograms.api.data;

import org.bukkit.configuration.ConfigurationSection;

public interface YamlData {

    void read(ConfigurationSection section, String name);

    void write(ConfigurationSection section, String name);
}
