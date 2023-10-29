package de.oliver.fancyholograms.api.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class BlockHologramData implements Data {

    public static Material DEFAULT_BLOCK = Material.GRASS_BLOCK;

    private Material block;

    public BlockHologramData(Material block) {
        this.block = block;
    }

    public BlockHologramData() {
    }

    public static BlockHologramData getDefault() {
        return new BlockHologramData(DEFAULT_BLOCK);
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        String blockStr = section.getString("block", "GRASS_BLOCK");
        block = Material.getMaterial(blockStr.toUpperCase());
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        section.set("block", block.name());
    }

    public Material getBlock() {
        return block;
    }

    public BlockHologramData setBlock(Material block) {
        this.block = block;
        return this;
    }

    @Override
    public Data copy() {
        return new BlockHologramData();
    }
}
