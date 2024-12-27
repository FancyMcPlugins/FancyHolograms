package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.api.hologram.Hologram;

import java.util.Collection;

public interface HologramStorage {

    /**
     * Saves a collection of holograms.
     *
     * @param holograms The holograms to save.
     * @param override  Whether to override existing holograms.
     */
    void saveBatch(Collection<Hologram> holograms, boolean override);

    /**
     * Saves a hologram.
     *
     * @param hologram The hologram to save.
     */
    void save(Hologram hologram);

    /**
     * Deletes a hologram.
     *
     * @param hologram The hologram to delete.
     */
    void delete(Hologram hologram);

    /**
     * Loads all holograms from all worlds
     *
     * @return A collection of all loaded holograms.
     */
    Collection<Hologram> loadAll();

    /**
     * Loads all holograms from a specific world
     *
     * @param world The world to load the holograms from.
     * @return A collection of all loaded holograms.
     */
    Collection<Hologram> loadAll(String world);
}
