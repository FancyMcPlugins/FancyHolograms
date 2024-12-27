package de.oliver.fancyholograms.storage;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancylib.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonStorage implements HologramStorage{

    private final JDB jdb;

    public JsonStorage() {
        this.jdb = new JDB("plugins/FancyHolograms/data/holograms");
    }

    @Override
    public void saveBatch(Collection<HologramData> holograms) {
        for (HologramData hologram : holograms) {
           save(hologram);
        }
    }

    @Override
    public void save(HologramData hologram) {
        try {
            jdb.set("worlds/" + hologram.getLocation().getWorld().getName() +  "/" + hologram.getType() + "/" + hologram.getName(), hologram);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to save hologram " + hologram.getName());
            FancyHolograms.get().getFancyLogger().error(e);
        }
    }

    @Override
    public void delete(HologramData hologram) {
        jdb.delete("worlds/" + hologram.getLocation().getWorld().getName() + "/" + hologram.getName());
    }

    @Override
    public Collection<HologramData> loadAll(String world) {
        List<HologramData> holograms = new ArrayList<>();

        try {
            holograms.addAll(jdb.getAll("worlds/" + world + "/text", TextHologramData.class));
            holograms.addAll(jdb.getAll("worlds/" + world + "/item", ItemHologramData.class));
            holograms.addAll(jdb.getAll("worlds/" + world + "/block", BlockHologramData.class));
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from world " + world);
            FancyHolograms.get().getFancyLogger().error(e);
        }

        return holograms;
    }
}
