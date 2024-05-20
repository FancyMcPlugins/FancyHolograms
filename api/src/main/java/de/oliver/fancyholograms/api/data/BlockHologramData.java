package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class BlockHologramData extends DisplayHologramData {

    public static Material DEFAULT_BLOCK = Material.GRASS_BLOCK;

    private Material block;

    public BlockHologramData(String name, Location location) {
        super(name, HologramType.BLOCK, location);
    }

    public Material getBlock() {
        return block;
    }

    public BlockHologramData setBlock(Material block) {
        this.block = block;
        return this;
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);
        block = Material.getMaterial(section.getString("block", "GRASS_BLOCK").toUpperCase());
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("block", block.name());
    }

    public static BlockHologramData getDefault(String name, Location location) {
        BlockHologramData blockHologramData = new BlockHologramData(name, location);
        blockHologramData
            .setBlock(DEFAULT_BLOCK)
            .setScale(DEFAULT_SCALE)
            .setShadowRadius(DEFAULT_SHADOW_RADIUS)
            .setShadowStrength(DEFAULT_SHADOW_STRENGTH)
            .setBillboard(DEFAULT_BILLBOARD)
            .setVisibilityDistance(DEFAULT_VISIBILITY_DISTANCE)
            .setVisibleByDefault(DEFAULT_IS_VISIBLE);

        return blockHologramData;
    }

    @Override
    public BlockHologramData copy(String name) {
        BlockHologramData blockHologramData = new BlockHologramData(name, getLocation());
        blockHologramData
            .setBlock(this.getBlock())
            .setScale(this.getScale())
            .setShadowRadius(this.getShadowRadius())
            .setShadowStrength(this.getShadowStrength())
            .setBillboard(this.getBillboard())
            .setTranslation(this.getTranslation())
            .setBrightness(this.getBrightness())
            .setVisibilityDistance(getVisibilityDistance())
            .setVisibleByDefault(isVisibleByDefault())
            .setLinkedNpcName(getLinkedNpcName());

        return blockHologramData;
    }
}
