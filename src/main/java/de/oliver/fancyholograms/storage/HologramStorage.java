package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.api.data.HologramData;

import java.util.Collection;

public interface HologramStorage {

    /**
     * Saves a collection of holograms.
     *
     * @param holograms The holograms to save.
     */
    void saveBatch(Collection<HologramData> holograms);

    /**
     * Saves a hologram.
     *
     * @param hologram The hologram to save.
     */
    void save(HologramData hologram);

    /**
     * Deletes a hologram.
     *
     * @param hologram The hologram to delete.
     */
    void delete(HologramData hologram);

    /**
     * Loads all holograms from a specific world
     *
     * @param world The world to load the holograms from.
     * @return A collection of all loaded holograms.
     */
    Collection<HologramData> loadAll(String world);
}
