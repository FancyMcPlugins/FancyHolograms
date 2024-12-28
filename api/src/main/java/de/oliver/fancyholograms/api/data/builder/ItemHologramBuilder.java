package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.data.ItemHologramData;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemHologramBuilder extends HologramBuilder{

    public ItemHologramBuilder(String name, Location location) {
        super();
        this.data = new ItemHologramData(name, location);
    }

    public static ItemHologramBuilder create(String name, Location location) {
        return new ItemHologramBuilder(name, location);
    }

    public ItemHologramBuilder item(ItemStack item) {
        ((ItemHologramData) data).setItemStack(item);
        return this;
    }

}
