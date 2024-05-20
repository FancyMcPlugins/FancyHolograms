package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.HologramType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

import java.util.Locale;

public class DisplayHologramData extends HologramData {

    public static final Display.Billboard DEFAULT_BILLBOARD = Display.Billboard.CENTER;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1, 1, 1);
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0, 0, 0);
    public static final float DEFAULT_SHADOW_RADIUS = 0.0f;
    public static final float DEFAULT_SHADOW_STRENGTH = 1.0f;

    private Display.Billboard billboard = DEFAULT_BILLBOARD;
    private Vector3f scale = new Vector3f(DEFAULT_SCALE);
    private Vector3f translation = new Vector3f(DEFAULT_TRANSLATION);
    private Display.Brightness brightness;
    private float shadowRadius = DEFAULT_SHADOW_RADIUS;
    private float shadowStrength = DEFAULT_SHADOW_STRENGTH;

    public DisplayHologramData(String name, HologramType type, Location location) {
        super(name, type, location);
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

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);
        scale = new Vector3f(
            (float) section.getDouble("scale_x", DEFAULT_SCALE.x),
            (float) section.getDouble("scale_y", DEFAULT_SCALE.y),
            (float) section.getDouble("scale_z", DEFAULT_SCALE.z)
        );

        shadowRadius = (float) section.getDouble("shadow_radius", DEFAULT_SHADOW_RADIUS);
        shadowStrength = (float) section.getDouble("shadow_strength", DEFAULT_SHADOW_STRENGTH);

        String billboardStr = section.getString("billboard", DEFAULT_BILLBOARD.name());
        billboard = switch (billboardStr.toLowerCase()) {
            case "fixed" -> Display.Billboard.FIXED;
            case "vertical" -> Display.Billboard.VERTICAL;
            case "horizontal" -> Display.Billboard.HORIZONTAL;
            default -> Display.Billboard.CENTER;
        };
    }

    @Override
    public void write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("scale_x", scale.x);
        section.set("scale_y", scale.y);
        section.set("scale_z", scale.z);
        section.set("shadow_radius", shadowRadius);
        section.set("shadow_strength", shadowStrength);
        section.set("billboard", billboard != Display.Billboard.CENTER ? billboard.name().toLowerCase(Locale.ROOT) : null);
    }

    // TODO: Discuss alternative ways to handle
    public static DisplayHologramData getDefault(String name, HologramType type, Location location) {
        DisplayHologramData displayHologramData = new DisplayHologramData(name, type, location);
        displayHologramData
            .setScale(DEFAULT_SCALE)
            .setShadowRadius(DEFAULT_SHADOW_RADIUS)
            .setShadowStrength(DEFAULT_SHADOW_STRENGTH)
            .setBillboard(DEFAULT_BILLBOARD)
            .setVisibilityDistance(DEFAULT_VISIBILITY_DISTANCE)
            .setVisibleByDefault(DEFAULT_IS_VISIBLE);

        return displayHologramData;
    }

    @Override
    public DisplayHologramData copy(String name) {
        DisplayHologramData displayHologramData = new DisplayHologramData(name, getType(), getLocation());
        displayHologramData
            .setScale(this.getScale())
            .setShadowRadius(this.getShadowRadius())
            .setShadowStrength(this.getShadowStrength())
            .setBillboard(this.getBillboard())
            .setTranslation(this.getTranslation())
            .setBrightness(this.getBrightness())
            .setVisibilityDistance(this.getVisibilityDistance())
            .setVisibleByDefault(this.isVisibleByDefault())
            .setLinkedNpcName(this.getLinkedNpcName());

        return displayHologramData;
    }
}
