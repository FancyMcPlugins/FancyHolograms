package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class HologramStackData extends HologramData {
    private List<HologramData> content = new ArrayList<>();

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public HologramStackData(String name, Location location) {
        super(name, HologramType.HOLOGRAM_STACK, location);
    }

    public List<HologramData> getContent() {
        return content;
    }

    public HologramStackData setContent(List<HologramData> content) {
        this.content = content;
        setHasChanges(true);
        return this;
    }

    public void addLine(HologramData data) {
        content.add(data);
        setHasChanges(true);
    }

    public void removeLine(int index) {
        content.remove(index);
        setHasChanges(true);
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);

        ConfigurationSection stackSection = section.getConfigurationSection("stack");
        if (stackSection != null) {
            for (String indexRaw : stackSection.getKeys(false)) {
                ConfigurationSection holoSection = stackSection.getConfigurationSection(indexRaw);
                if (holoSection == null) {
                    continue;
                }

                String typeName = holoSection.getString("type");
                if (typeName == null) {
                    FancyHologramsPlugin.get().getPlugin().getLogger().warning("HologramType was not saved");
                    continue;
                }

                HologramType type = HologramType.getByName(typeName);
                if (type == null) {
                    FancyHologramsPlugin.get().getPlugin().getLogger().warning("Could not parse HologramType");
                    continue;
                }

                DisplayHologramData displayData = null;
                switch (type) {
                    case TEXT -> displayData = new TextHologramData(name, new Location(null, 0, 0, 0));
                    case ITEM -> displayData = new ItemHologramData(name, new Location(null, 0, 0, 0));
                    case BLOCK -> displayData = new BlockHologramData(name, new Location(null, 0, 0, 0));
                }
                displayData.read(holoSection, name);

                int index = Integer.parseInt(indexRaw);
                content.set(index, displayData);
            }
        }
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        super.write(section, name);

        for (int i = 0; i < content.size(); i++) {
            HologramData hologramData = content.get(i);
            ConfigurationSection dataSection = section.createSection("stack." + i);
            hologramData.write(dataSection, hologramData.getName());
        }
    }

    @Override
    public HologramStackData copy(String name) {
        HologramStackData hologramStackData = new HologramStackData(name, getLocation());
        hologramStackData
            .setContent(this.getContent())
            .setVisibilityDistance(this.getVisibilityDistance())
            .setVisibility(this.getVisibility())
            .setPersistent(this.isPersistent())
            .setLinkedNpcName(this.getLinkedNpcName());

        return hologramStackData;
    }
}
