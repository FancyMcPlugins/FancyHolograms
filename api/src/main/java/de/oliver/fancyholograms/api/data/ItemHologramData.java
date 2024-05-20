package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemHologramData extends DisplayHologramData {

    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.APPLE);

    private ItemStack item;

    public ItemHologramData(String name, Location location) {
        super(name, HologramType.ITEM, location);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemHologramData setItemStack(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);
        item = section.getItemStack("item", DEFAULT_ITEM);
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("item", item);
    }

    public static ItemHologramData getDefault(String name, Location location) {
        ItemHologramData itemHologramData = new ItemHologramData(name, location);
        itemHologramData
            .setItemStack(DEFAULT_ITEM)
            .setScale(DEFAULT_SCALE)
            .setShadowRadius(DEFAULT_SHADOW_RADIUS)
            .setShadowStrength(DEFAULT_SHADOW_STRENGTH)
            .setBillboard(DEFAULT_BILLBOARD)
            .setVisibilityDistance(DEFAULT_VISIBILITY_DISTANCE)
            .setVisibleByDefault(DEFAULT_IS_VISIBLE);

        return itemHologramData;
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
            .setVisibilityDistance(getVisibilityDistance())
            .setVisibleByDefault(isVisibleByDefault())
            .setLinkedNpcName(getLinkedNpcName());

        return itemHologramData;
    }
}
