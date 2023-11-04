package de.oliver.fancyholograms.api.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemHologramData implements Data {

    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.APPLE);

    private ItemStack item;

    public ItemHologramData(ItemStack item) {
        this.item = item;
    }

    public ItemHologramData() {
    }

    public static ItemHologramData getDefault() {
        return new ItemHologramData(DEFAULT_ITEM);
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        section.set("item", item);
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        item = section.getItemStack("item", DEFAULT_ITEM);
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemHologramData setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public Data copy() {
        return new ItemHologramData(
                item.clone()
        );
    }
}
