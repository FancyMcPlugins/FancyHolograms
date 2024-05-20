package de.oliver.fancyholograms.api;

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
     * Loads all holograms.
     *
     * @return A collection of all loaded holograms.
     */
    Collection<Hologram> loadAll();
}
