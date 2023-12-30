package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancylib.FancyLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

import java.util.Locale;

public class DisplayHologramData implements Data {

    public static final Display.Billboard DEFAULT_BILLBOARD = Display.Billboard.CENTER;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1, 1, 1);
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0, 0, 0);
    public static final float DEFAULT_SHADOW_RADIUS = 0.0f;
    public static final float DEFAULT_SHADOW_STRENGTH = 1.0f;
    public static final int DEFAULT_VISIBILITY_DISTANCE = -1;

    private Location location;
    private Display.Billboard billboard = DEFAULT_BILLBOARD;
    private Vector3f scale = new Vector3f(DEFAULT_SCALE);
    private Vector3f translation = new Vector3f(DEFAULT_TRANSLATION);
    private Display.Brightness brightness;
    private float shadowRadius = DEFAULT_SHADOW_RADIUS;
    private float shadowStrength = DEFAULT_SHADOW_STRENGTH;
    private int visibilityDistance = DEFAULT_VISIBILITY_DISTANCE;
    private String linkedNpcName;

    public DisplayHologramData(Location location, Display.Billboard billboard, Vector3f scale, Vector3f translation, Display.Brightness brightness, float shadowRadius, float shadowStrength, int visibilityDistance, String linkedNpcName) {
        this.location = location;
        this.billboard = billboard;
        this.scale = scale;
        this.translation = translation;
        this.brightness = brightness;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.visibilityDistance = visibilityDistance;
        this.linkedNpcName = linkedNpcName;
    }

    public DisplayHologramData() {
    }

    public static DisplayHologramData getDefault(Location location) {
        return new DisplayHologramData(
                location,
                DEFAULT_BILLBOARD,
                DEFAULT_SCALE,
                DEFAULT_TRANSLATION,
                null,
                DEFAULT_SHADOW_RADIUS,
                DEFAULT_SHADOW_STRENGTH,
                DEFAULT_VISIBILITY_DISTANCE,
                null
        );
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        section.set("location.world", location.getWorld().getName());
        section.set("location.x", location.x());
        section.set("location.y", location.y());
        section.set("location.z", location.z());
        section.set("location.yaw", location.getYaw());
        section.set("location.pitch", location.getPitch());

        section.set("scale_x", scale.x);
        section.set("scale_y", scale.y);
        section.set("scale_z", scale.z);
        section.set("shadow_radius", shadowRadius);
        section.set("shadow_strength", shadowStrength);
        section.set("visibility_distance", visibilityDistance);


        if (billboard == Display.Billboard.CENTER) {
            section.set("billboard", null);
        } else {
            section.set("billboard", billboard.name().toLowerCase(Locale.ROOT));
        }

        section.set("linkedNpc", linkedNpcName);
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

        scale = new Vector3f(
                (float) section.getDouble("scale_x", DEFAULT_SCALE.x),
                (float) section.getDouble("scale_y", DEFAULT_SCALE.y),
                (float) section.getDouble("scale_z", DEFAULT_SCALE.z)
        );

        shadowRadius = (float) section.getDouble("shadow_radius", DEFAULT_SHADOW_RADIUS);
        shadowStrength = (float) section.getDouble("shadow_strength", DEFAULT_SHADOW_STRENGTH);
        visibilityDistance = section.getInt("visibility_distance", DEFAULT_VISIBILITY_DISTANCE);
        linkedNpcName = section.getString("linkedNpc");

        String billboardStr = section.getString("billboard", DisplayHologramData.DEFAULT_BILLBOARD.name());
        billboard = switch (billboardStr.toLowerCase()) {
            case "fixed" -> Display.Billboard.FIXED;
            case "vertical" -> Display.Billboard.VERTICAL;
            case "horizontal" -> Display.Billboard.HORIZONTAL;
            default -> Display.Billboard.CENTER;
        };
    }

    public Location getLocation() {
        return location;
    }

    public DisplayHologramData setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public DisplayHologramData setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public Vector3f getScale() {
        return scale;
    }

    public DisplayHologramData setScale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public DisplayHologramData setTranslation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    public Display.Brightness getBrightness() {
        return brightness;
    }

    public DisplayHologramData setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        return this;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public DisplayHologramData setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public DisplayHologramData setShadowStrength(float shadowStrength) {
        this.shadowStrength = shadowStrength;
        return this;
    }

    public int getVisibilityDistance() {
        if (visibilityDistance > 0) {
            return visibilityDistance;
        }

        return FancyHologramsPlugin.get().getHologramConfiguration().getDefaultVisibilityDistance();
    }

    public DisplayHologramData setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
        return this;
    }

    public String getLinkedNpcName() {
        return linkedNpcName;
    }

    public DisplayHologramData setLinkedNpcName(String linkedNpcName) {
        this.linkedNpcName = linkedNpcName;
        return this;
    }

    @Override
    public Data copy() {
        return new DisplayHologramData(
                location.clone(),
                billboard,
                scale,
                translation,
                brightness,
                shadowRadius,
                shadowStrength,
                visibilityDistance,
                linkedNpcName
        );
    }
}
