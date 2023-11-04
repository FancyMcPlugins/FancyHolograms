package de.oliver.fancyholograms.api.data;

import org.bukkit.configuration.ConfigurationSection;

public interface Data {
    void write(ConfigurationSection section, String name);

    void read(ConfigurationSection section, String name);

    Data copy();
}
