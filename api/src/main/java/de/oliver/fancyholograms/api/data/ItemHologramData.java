package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class ItemHologramData extends DisplayHologramData {

    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.APPLE);

    private ItemStack item = DEFAULT_ITEM;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public ItemHologramData(String name, Location location) {
        super(name, HologramType.ITEM, location);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemHologramData setItemStack(ItemStack item) {
        if (!Objects.equals(this.item, item)) {
            this.item = item;
            setHasChanges(true);
        }

        return this;
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        item = section.getItemStack("item", DEFAULT_ITEM);

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("item", item);

        return true;
    }

    @Override
    public ItemHologramData copy(String name) {
        ItemHologramData itemHologramData = new ItemHologramData(name, getLocation());
        itemHologramData
                .setItemStack(this.getItemStack())
                .setScale(this.getScale())
                .setShadowRadius(this.getShadowRadius())
                .setShadowStrength(this.getShadowStrength())
                .setBillboard(this.getBillboard())
                .setTranslation(this.getTranslation())
                .setBrightness(this.getBrightness())
                .setVisibilityDistance(this.getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(this.getLinkedNpcName());

        return itemHologramData;
    }
}
