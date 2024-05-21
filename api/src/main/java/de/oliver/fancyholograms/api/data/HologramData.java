package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.HologramType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HologramData implements Data {


    @NotNull
    private final String name;

    @NotNull
    private final DisplayHologramData displayData;

    @NotNull
    private final HologramType type;

    @NotNull
    private final Data typeData;

    private final boolean persistent;


    /**
     * Constructs a new HologramData with the given name.
     *
     * @param name the name of the hologram
     */
    public HologramData(@NotNull String name, @NotNull DisplayHologramData displayData, @NotNull HologramType type, @NotNull Data typeData, boolean persistent) {
        this.name = name;
        this.displayData = displayData;
        this.type = type;
        this.typeData = typeData;
        this.persistent = persistent;
    }


    /**
     * Constructs a copy of an existing HologramData with a new name.
     *
     * @param name  the name for the new HologramData
     * @param other the HologramData to copy from
     */
    public HologramData(@NotNull final String name, @NotNull final HologramData other) {
        this.name = name;
        this.displayData = (DisplayHologramData) other.getDisplayData().copy();
        this.type = other.getType();
        this.typeData = other.getTypeData().copy();
        this.persistent = other.isPersistent();
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        section.set("type", type.name());

        displayData.write(section, name);
        typeData.write(section, name);
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        displayData.read(section, name);
        typeData.read(section, name);
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull DisplayHologramData getDisplayData() {
        return displayData;
    }

    public @NotNull HologramType getType() {
        return type;
    }

    public @NotNull Data getTypeData() {
        return typeData;
    }

    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Returns a copy of this HologramData.
     *
     * @return a copy of this HologramData
     */
    public @NotNull HologramData copy() {
        return new HologramData(getName(), this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HologramData that = (HologramData) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

}
