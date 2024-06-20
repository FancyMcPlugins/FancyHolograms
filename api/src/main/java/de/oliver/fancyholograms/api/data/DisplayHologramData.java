package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.HologramType;
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

    /**
     * @param name     Name of hologram
     * @param type     Type of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public DisplayHologramData(String name, HologramType type, Location location) {
        super(name, type, location);
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public DisplayHologramData setBillboard(Display.Billboard billboard) {
        if (!this.billboard.equals(billboard)) {
            this.billboard = billboard;
            setHasChanges(true);
        }

        return this;
    }

    public Vector3f getScale() {
        return scale;
    }

    public DisplayHologramData setScale(Vector3f scale) {
        if (!this.scale.equals(scale)) {
            this.scale = scale;
            setHasChanges(true);
        }

        return this;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public DisplayHologramData setTranslation(Vector3f translation) {
        if (!this.translation.equals(translation)) {
            this.translation = translation;
            setHasChanges(true);
        }

        return this;
    }

    public Display.Brightness getBrightness() {
        return brightness;
    }

    public DisplayHologramData setBrightness(Display.Brightness brightness) {
        if (!this.brightness.equals(brightness)) {
            this.brightness = brightness;
            setHasChanges(true);
        }

        return this;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public DisplayHologramData setShadowRadius(float shadowRadius) {
        if (this.shadowRadius != shadowRadius) {
            this.shadowRadius = shadowRadius;
            setHasChanges(true);
        }

        return this;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public DisplayHologramData setShadowStrength(float shadowStrength) {
        if (this.shadowStrength != shadowStrength) {
            this.shadowStrength = shadowStrength;
            setHasChanges(true);
        }

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
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(this.getLinkedNpcName());

        return displayHologramData;
    }
}
