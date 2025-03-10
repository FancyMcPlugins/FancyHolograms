package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockHologramBuilder extends HologramBuilder{

    private BlockHologramBuilder(String name, Location location) {
        super();
        this.data = new BlockHologramData(name, location);
    }

    /**
     * Creates a new instance of BlockHologramBuilder with the specified name and location.
     *
     * @param name the name of the block hologram
     * @param location the location of the block hologram
     * @return a new instance of BlockHologramBuilder
     */
    public static BlockHologramBuilder create(String name, Location location) {
        return new BlockHologramBuilder(name, location);
    }
    
    public BlockHologramBuilder block(Material block) {
        ((BlockHologramData) data).setBlock(block);
        return this;
    }

}
