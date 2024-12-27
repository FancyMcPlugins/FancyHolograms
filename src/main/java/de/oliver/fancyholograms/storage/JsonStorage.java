package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancylib.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JsonStorage implements HologramStorage{

    private final JDB jdb;

    public JsonStorage() {
        this.jdb = new JDB("data/holograms");
    }

    @Override
    public void saveBatch(Collection<Hologram> holograms, boolean override) {
        for (Hologram hologram : holograms) {
           save(hologram);
        }
    }

    @Override
    public void save(Hologram hologram) {
        try {
            jdb.set("worlds/" + hologram.getData().getLocation().getWorld().getName() + "/" + hologram.getData().getName(), hologram);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to save hologram " + hologram.getData().getName());
            FancyHolograms.get().getFancyLogger().error(e);
        }
    }

    @Override
    public void delete(Hologram hologram) {
        jdb.delete("worlds/" + hologram.getData().getLocation().getWorld().getName() + "/" + hologram.getData().getName());
    }

    @Override
    public Collection<Hologram> loadAll() {
        try {
            return jdb.getAll("worlds", Hologram.class);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms");
            FancyHolograms.get().getFancyLogger().error(e);
        }

        return new ArrayList<>();
    }

    @Override
    public Collection<Hologram> loadAll(String world) {
        try {
            return jdb.getAll("worlds/"+world, Hologram.class);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from world " + world);
            FancyHolograms.get().getFancyLogger().error(e);
        }

        return new ArrayList<>();
    }
}
