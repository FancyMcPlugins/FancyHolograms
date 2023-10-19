package de.oliver.fancyholograms.api;

import java.util.Collection;
import java.util.Optional;

public interface HologramManager {

    Optional<Hologram> getHologram(String name);

    Collection<Hologram> getHolograms();

    void addHologram(Hologram hologram);

    void removeHologram(Hologram hologram);

    Hologram create(HologramData hologramData);

    void loadHolograms();

    void saveHolograms();

    void reloadHolograms();

}
