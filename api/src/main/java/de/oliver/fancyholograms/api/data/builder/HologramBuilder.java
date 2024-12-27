package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;

public class HologramBuilder {

    private final HologramData data;

    private HologramBuilder(HologramType type, String name, Location location) {
        switch (type){
            case TEXT -> data = new TextHologramData(name, location);
            case ITEM -> data = new ItemHologramData(name, location);
            case BLOCK -> data = new BlockHologramData(name, location);
            default -> throw new UnsupportedOperationException("Unsupported hologram type: " + type);
        }
    }

    public static HologramBuilder create(HologramType type, String name, Location location) {
        return new HologramBuilder(type, name, location);
    }

    public Hologram build() {
        return FancyHolograms.get().getHologramFactory().apply(data);
    }

}
