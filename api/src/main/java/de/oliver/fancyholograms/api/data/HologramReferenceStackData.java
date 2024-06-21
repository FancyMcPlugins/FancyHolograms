package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class HologramReferenceStackData extends HologramData {
    private List<String> holograms = new ArrayList<>();

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public HologramReferenceStackData(String name, Location location) {
        super(name, HologramType.HOLOGRAM_STACK, location);
    }

    public List<String> getHolograms() {
        return holograms;
    }

    public HologramReferenceStackData setHolograms(List<String> holograms) {
        this.holograms = holograms;
        setHasChanges(true);
        return this;
    }

    public void addLine(String hologramName) {
        holograms.add(hologramName);
        setHasChanges(true);
    }

    public void removeLine(int index) {
        holograms.remove(index);
        setHasChanges(true);
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);

        ConfigurationSection stackSection = section.getConfigurationSection("stack");
        if (stackSection != null) {

        }
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        super.write(section, name);

        for (int i = 0; i < holograms.size(); i++) {
            section.set("stack." + i, holograms.get(i));
        }
    }

    @Override
    public HologramReferenceStackData copy(String name) {
        HologramReferenceStackData hologramStackData = new HologramReferenceStackData(name, getLocation());
        hologramStackData
            .setHolograms(this.getHolograms())
            .setVisibilityDistance(this.getVisibilityDistance())
            .setVisibility(this.getVisibility())
            .setPersistent(this.isPersistent())
            .setLinkedNpcName(this.getLinkedNpcName());

        return hologramStackData;
    }
}
