package de.oliver.fancyholograms.registry;

import de.oliver.fancyholograms.api.HologramRegistry;
import de.oliver.fancyholograms.api.hologram.Hologram;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HologramRegistryImpl implements HologramRegistry {

    private final Map<String, Hologram> holograms;

    public HologramRegistryImpl() {
        this.holograms = new ConcurrentHashMap<>();
    }

   @Override
    public boolean register(Hologram hologram) {
        return holograms.putIfAbsent(hologram.getName(), hologram) != null;
    }

    @Override
    public boolean unregister(Hologram hologram) {
        return holograms.remove(hologram.getName()) != null;
    }

    @Override
    public boolean contains(String name) {
        return holograms.containsKey(name);
    }

    @Override
    public Optional<Hologram> get(String name) {
        return Optional.ofNullable(holograms.get(name));
    }

    @Override
    public Hologram mustGet(String name) {
        if (!contains(name)) {
            throw new IllegalArgumentException("Hologram with name " + name + " does not exist!");
        }

        return holograms.get(name);
    }

    @Override
    public Collection<Hologram> getAll() {
        return Collections.unmodifiableCollection(holograms.values());
    }

    @Override
    public Collection<Hologram> getAllPersistent() {
        return getAll()
                .stream()
                .filter(hologram -> hologram.getData().isPersistent())
                .toList();
    }

    @Override
    public void clear() {
        holograms.clear();
    }
}
