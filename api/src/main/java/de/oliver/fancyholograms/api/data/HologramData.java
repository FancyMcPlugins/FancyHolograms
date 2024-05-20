package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancylib.FancyLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HologramData implements YamlData {

    public static final int DEFAULT_VISIBILITY_DISTANCE = -1;
    public static final boolean DEFAULT_IS_VISIBLE = true;

    private final String name;
    private final HologramType type;
    private Location location;
    private int visibilityDistance = DEFAULT_VISIBILITY_DISTANCE;
    private boolean visibleByDefault = DEFAULT_IS_VISIBLE;
    private String linkedNpcName;

    public HologramData(String name, HologramType type, Location location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull HologramType getType() {
        return type;
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public HologramData setLocation(@Nullable Location location) {
        this.location = location;
        return this;
    }

    public int getVisibilityDistance() {
        if (visibilityDistance > 0) {
            return visibilityDistance;
        }

        return FancyHologramsPlugin.get().getHologramConfiguration().getDefaultVisibilityDistance();
    }

    public HologramData setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
        return this;
    }

    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    public HologramData setVisibleByDefault(boolean visibleByDefault) {
        this.visibleByDefault = visibleByDefault;
        return this;
    }

    public String getLinkedNpcName() {
        return linkedNpcName;
    }

    public HologramData setLinkedNpcName(String linkedNpcName) {
        this.linkedNpcName = linkedNpcName;
        return this;
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        String worldName = section.getString("location.world", "world");
        float x = (float) section.getDouble("location.x", 0);
        float y = (float) section.getDouble("location.y", 0);
        float z = (float) section.getDouble("location.z", 0);
        float yaw = (float) section.getDouble("location.yaw", 0);
        float pitch = (float) section.getDouble("location.pitch", 0);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            FancyLib.getPlugin().getLogger().info("Trying to load the world: '" + worldName + "'");
            world = new WorldCreator(worldName).createWorld();
        }

        if (world == null) {
            FancyLib.getPlugin().getLogger().info("Could not load hologram '" + name + "', because the world '" + worldName + "' is not loaded");
            return;
        }

        location = new Location(world, x, y, z, yaw, pitch);
        visibilityDistance = section.getInt("visibility_distance", DEFAULT_VISIBILITY_DISTANCE);
        visibleByDefault = section.getBoolean("visible_by_default", DEFAULT_IS_VISIBLE);
        linkedNpcName = section.getString("linkedNpc");
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        section.set("type", type.name());
        section.set("location.world", location.getWorld().getName());
        section.set("location.x", location.x());
        section.set("location.y", location.y());
        section.set("location.z", location.z());
        section.set("location.yaw", location.getYaw());
        section.set("location.pitch", location.getPitch());

        section.set("visibility_distance", visibilityDistance);
        section.set("visible_by_default", visibleByDefault);
        section.set("linkedNpc", linkedNpcName);
    }

    public static HologramData getDefault(String name, Location location) {
        return getDefault(name, HologramType.TEXT, location);
    }

    public static HologramData getDefault(String name, HologramType type, Location location) {
        return new HologramData(name, type, location)
            .setVisibilityDistance(DEFAULT_VISIBILITY_DISTANCE)
            .setVisibleByDefault(DEFAULT_IS_VISIBLE);
    }

    public HologramData copy(String name) {
        return new HologramData(name, type, location)
            .setVisibilityDistance(this.getVisibilityDistance())
            .setVisibleByDefault(this.isVisibleByDefault())
            .setLinkedNpcName(this.getLinkedNpcName());
    }
}
