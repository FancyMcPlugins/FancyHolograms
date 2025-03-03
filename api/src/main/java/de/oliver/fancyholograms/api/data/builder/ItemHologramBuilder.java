package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.data.ItemHologramData;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemHologramBuilder extends HologramBuilder{

    private ItemHologramBuilder(String name, Location location) {
        super();
        this.data = new ItemHologramData(name, location);
    }

    /**
     * Creates a new instance of ItemHologramBuilder with the specified name and location.
     *
     * @param name the name of the item hologram
     * @param location the location of the item hologram
     * @return a new instance of ItemHologramBuilder
     */
    public static ItemHologramBuilder create(String name, Location location) {
        return new ItemHologramBuilder(name, location);
    }

    public ItemHologramBuilder item(ItemStack item) {
        ((ItemHologramData) data).setItemStack(item);
        return this;
    }

}
