package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class DroppedItemHologramData extends HologramData {

    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.APPLE);

    private ItemStack item = DEFAULT_ITEM;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public DroppedItemHologramData(String name, Location location) {
        super(name, HologramType.DROPPED_ITEM, location);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public DroppedItemHologramData setItemStack(ItemStack item) {
        this.item = item;
        setHasChanges(true);
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

    @Override
    public DroppedItemHologramData copy(String name) {
        DroppedItemHologramData droppedItemHologramData = new DroppedItemHologramData(name, getLocation());
        droppedItemHologramData
            .setItemStack(this.getItemStack())
            .setVisibilityDistance(this.getVisibilityDistance())
            .setVisibility(this.getVisibility())
            .setPersistent(this.isPersistent())
            .setLinkedNpcName(this.getLinkedNpcName());

        return droppedItemHologramData;
    }
}
