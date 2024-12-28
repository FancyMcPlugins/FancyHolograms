package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockHologramBuilder extends HologramBuilder{

    public BlockHologramBuilder(String name, Location location) {
        super();
        this.data = new BlockHologramData(name, location);
    }

    public static BlockHologramBuilder create(String name, Location location) {
        return new BlockHologramBuilder(name, location);
    }
    
    public BlockHologramBuilder block(Material block) {
        ((BlockHologramData) data).setBlock(block);
        return this;
    }

}
