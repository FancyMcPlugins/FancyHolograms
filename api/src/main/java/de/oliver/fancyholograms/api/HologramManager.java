package de.oliver.fancyholograms.api;

import de.oliver.fancyholograms.api.data.HologramData;

import java.util.Collection;
import java.util.Optional;

public interface HologramManager {

    Optional<Hologram> getHologram(String name);

    Collection<Hologram> getPersistentHolograms();

    Collection<Hologram> getHolograms();

    void addHologram(Hologram hologram);

    void removeHologram(Hologram hologram);

    Hologram create(HologramData hologramData);

    void loadHolograms();

    void saveHolograms();

    void reloadHolograms();

}
