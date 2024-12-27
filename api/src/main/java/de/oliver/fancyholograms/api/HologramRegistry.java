package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.hologram.Hologram;

import java.util.Collection;
import java.util.Optional;

/**
 * An interface for managing the registration and retrieval of holograms.
 * Provides methods to register, unregister, and query holograms by their name.
 */
public interface HologramRegistry {

    /**
     * Registers a hologram in the registry.
     *
     * @param hologram the hologram to be registered
     * @return {@code true} if the registration was successful, otherwise {@code false}
     */
    boolean register(Hologram hologram);

    /**
     * Unregisters the specified hologram from the registry.
     *
     * @param hologram the hologram to be unregistered
     * @return {@code true} if the hologram was successfully unregistered, otherwise {@code false}
     */
    boolean unregister(Hologram hologram);

    /**
     * Checks if a hologram with the specified name exists in the registry.
     *
     * @param name the name of the hologram to check for existence
     * @return {@code true} if a hologram with the specified name exists, otherwise {@code false}
     */
    boolean contains(String name);

    /**
     * Retrieves a hologram by its name from the registry.
     *
     * @param name the name of the hologram to retrieve
     * @return an {@code Optional} containing the hologram if found, or an empty {@code Optional} if no hologram exists with the specified name
     */
    Optional<Hologram> get(String name);

    /**
     * Retrieves a hologram by its name from the registry, ensuring that the hologram exists.
     * If no hologram exists with the specified name, this method will throw an exception.
     *
     * @param name the name of the hologram to retrieve
     * @return the hologram associated with the specified name
     * @throws IllegalArgumentException if no hologram exists with the given name
     */
    Hologram mustGet(String name);

    /**
     * Retrieves all holograms currently registered in the registry.
     *
     * @return a collection containing all registered holograms
     */
    Collection<Hologram> getAll();

    /**
     * Retrieves all persistent holograms currently registered in the registry.
     *
     * @return a collection containing all persistent holograms
     */
    Collection<Hologram> getAllPersistent();

    /**
     * Removes all holograms from the registry, effectively clearing its contents.
     */
    void clear();
}
