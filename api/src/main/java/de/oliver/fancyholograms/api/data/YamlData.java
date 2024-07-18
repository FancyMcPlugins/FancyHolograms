package de.oliver.fancyholograms.api.data;

import org.bukkit.configuration.ConfigurationSection;

public interface YamlData {

    /**
     * Reads the data from the given configuration section.
     *
     * @return Whether the data was read successfully.
     */
    boolean read(ConfigurationSection section, String name);

    /**
     * Writes the data to the given configuration section.
     *
     * @return Whether the data was written successfully.
     */
    boolean write(ConfigurationSection section, String name);
}
