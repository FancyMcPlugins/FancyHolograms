package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class BlockHologramData extends DisplayHologramData {

    public static Material DEFAULT_BLOCK = Material.GRASS_BLOCK;

    private Material block = DEFAULT_BLOCK;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public BlockHologramData(String name, Location location) {
        super(name, HologramType.BLOCK, location);
    }

    public Material getBlock() {
        return block;
    }

    public BlockHologramData setBlock(Material block) {
        if (!Objects.equals(this.block, block)) {
            this.block = block;
            setHasChanges(true);
        }

        return this;
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        block = Material.getMaterial(section.getString("block", "GRASS_BLOCK").toUpperCase());

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("block", block.name());

        return true;
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
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(getLinkedNpcName());

        return blockHologramData;
    }
}
