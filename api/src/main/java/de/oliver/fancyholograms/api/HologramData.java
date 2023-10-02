package de.oliver.fancyholograms.api;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import org.joml.Vector3f;

/**
 * This class represents a Hologram with all its properties like name, text, location,
 * billboard type, background color, scale, shadow radius, shadow strength, text shadow state,
 * text update interval and the linked NPC's name.
 */
public final class HologramData {

    public static final Billboard DEFAULT_BILLBOARD = Billboard.CENTER;
    public static final TextDisplay.TextAlignment DEFAULT_TEXT_ALIGNMENT = TextDisplay.TextAlignment.CENTER;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1, 1, 1);
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0, 0, 0);
    public static final float DEFAULT_SHADOW_RADIUS = 0.0f;
    public static final float DEFAULT_SHADOW_STRENGTH = 1.0f;
    public static final boolean DEFAULT_TEXT_SHADOW_STATE = false;
    public static final int DEFAULT_TEXT_UPDATE_INTERVAL = -1;


    @NotNull
    private final String name;
    @NotNull
    private final List<String> text = new ArrayList<>();

    @Nullable
    private Location location;
    @NotNull
    private Billboard billboard = DEFAULT_BILLBOARD;
    @Nullable
    private TextColor background;
    @NotNull
    private TextDisplay.TextAlignment textAlignment = DEFAULT_TEXT_ALIGNMENT;

    private Vector3f scale = new Vector3f(DEFAULT_SCALE);
    private Vector3f translation = new Vector3f(DEFAULT_TRANSLATION);
    private Brightness brightness;
    private float shadowRadius = DEFAULT_SHADOW_RADIUS;
    private float shadowStrength = DEFAULT_SHADOW_STRENGTH;
    private boolean textHasShadow = DEFAULT_TEXT_SHADOW_STATE;
    private int textUpdateInterval = DEFAULT_TEXT_UPDATE_INTERVAL;
    private int visibilityDistance;


    @Nullable
    private String linkedNpcName;


    /**
     * Constructs a new HologramData with the given name.
     *
     * @param name the name of the hologram
     */
    public HologramData(@NotNull final String name) {
        this.name = name;
    }

    /**
     * Constructs a copy of an existing HologramData with a new name.
     *
     * @param name  the name for the new HologramData
     * @param other the HologramData to copy from
     */
    public HologramData(@NotNull final String name, @NotNull final HologramData other) {
        this.name = name;
        this.text.addAll(other.getText());

        this.location = other.getLocation() == null ? null : other.getLocation().clone();
        this.billboard = other.getBillboard();
        this.background = other.getBackground();
        this.textAlignment = other.getTextAlignment();
        this.scale = new Vector3f(other.getScale());
        this.translation = other.getTranslation();
        this.brightness = other.getBrightness();
        this.shadowRadius = other.getShadowRadius();
        this.shadowStrength = other.getShadowStrength();
        this.textHasShadow = other.isTextHasShadow();
        this.textUpdateInterval = other.getTextUpdateInterval();
        this.linkedNpcName = other.getLinkedNpcName();
        this.visibilityDistance = other.getVisibilityDistance();
    }


    /**
     * Returns the name of this HologramData.
     *
     * @return the name of the hologram
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Returns the text of this HologramData.
     *
     * @return a read-only view of the text of the hologram
     */
    public @NotNull
    @UnmodifiableView List<String> getText() {
        return Collections.unmodifiableList(this.text);
    }

    /**
     * Sets the text of this HologramData.
     *
     * @param text the new text of the hologram
     */
    public void setText(@NotNull final Collection<String> text) {
        this.text.clear();
        this.text.addAll(text);
    }


    /**
     * Returns the location of this HologramData.
     *
     * @return the location of the hologram, or null if not set
     */
    public @Nullable Location getLocation() {
        return this.location;
    }

    /**
     * Sets the location of this HologramData.
     *
     * @param location the new location of the hologram
     */
    public void setLocation(@Nullable final Location location) {
        this.location = location;
    }

    /**
     * Returns the billboard setting of this HologramData.
     *
     * @return the billboard setting of the hologram
     */
    public @NotNull Billboard getBillboard() {
        return this.billboard;
    }

    /**
     * Sets the billboard setting of this HologramData.
     *
     * @param billboard the new billboard setting for the hologram
     */
    public void setBillboard(@NotNull final Billboard billboard) {
        this.billboard = billboard;
    }

    /**
     * Returns the background color of this HologramData.
     *
     * @return the background color of the hologram, or null if not set
     */
    public @Nullable TextColor getBackground() {
        return this.background;
    }

    /**
     * Sets the background color of this HologramData.
     *
     * @param background the new background color for the hologram
     */
    public void setBackground(@Nullable final TextColor background) {
        this.background = background;
    }

    /**
     * Returns the text alignment of this HologramData
     *
     * @return the text alignment of the hologram
     */
    public TextDisplay.@NotNull TextAlignment getTextAlignment() {
        return textAlignment;
    }

    /**
     * Sets the text alignment of this HologramData
     *
     * @param textAlignment the new text alignment for the hologram
     */
    public void setTextAlignment(TextDisplay.@NotNull TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    /**
     * Returns the translation of this HologramData.
     *
     * @return the translation of the hologram
     */
    public Vector3f getScale() {
        return this.scale;
    }

    /**
     * Sets the scale of this HologramData.
     *
     * @param scale the new scale for the hologram
     */
    public void setScale(final float scale) {
        this.scale.set(scale, scale, scale);
    }

    /**
     * Sets the scale of this HologramData.
     *
     * @param scaleX the new x scale for the hologram
     * @param scaleY the new y scale for the hologram
     * @param scaleZ the new z scale for the hologram
     */
    public void setScale(final float scaleX, final float scaleY, final float scaleZ) {
        this.scale.set(scaleX, scaleY, scaleZ);
    }


    /**
     * Returns the translation of this HologramData.
     *
     * @return the translation of the hologram
     */
    public Vector3f getTranslation() {
        return this.translation;
    }

    /**
     * Sets the translation of this HologramData.
     *
     * @param translation the translation for the hologram
     */
    public void setTranslation(final Vector3f translation) {
        this.translation = translation;
    }


    /**
     * Returns the brightness of this HologramData.
     *
     * @return the brightness of the hologram
     */
    public Brightness getBrightness() {
        return this.brightness;
    }

    /**
     * Sets the brightness of this HologramData.
     *
     * @param brightness the brightness for the hologram
     */
    public void setBrightness(final Brightness brightness) {
        this.brightness = brightness;
    }

    /**
     * Returns the shadow radius of this HologramData.
     *
     * @return the shadow radius of the hologram
     */
    public float getShadowRadius() {
        return this.shadowRadius;
    }

    /**
     * Sets the shadow radius of this HologramData.
     *
     * @param shadowRadius the new shadow radius for the hologram
     */
    public void setShadowRadius(final float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    /**
     * Returns the shadow strength of this HologramData.
     *
     * @return the shadow strength of the hologram
     */
    public float getShadowStrength() {
        return this.shadowStrength;
    }

    /**
     * Sets the shadow strength of this HologramData.
     *
     * @param shadowStrength the new shadow strength for the hologram
     */
    public void setShadowStrength(final float shadowStrength) {
        this.shadowStrength = shadowStrength;
    }

    /**
     * Returns the text shadow state of this HologramData.
     *
     * @return true if the text of the hologram has shadow, false otherwise
     */
    public boolean isTextHasShadow() {
        return this.textHasShadow;
    }

    /**
     * Sets the text shadow state of this HologramData.
     *
     * @param textHasShadow the new text shadow state for the hologram
     */
    public void setTextHasShadow(final boolean textHasShadow) {
        this.textHasShadow = textHasShadow;
    }

    /**
     * Returns the text update interval, in ticks, of this HologramData.
     *
     * @return the text update interval of the hologram in ticks
     */
    public int getTextUpdateInterval() {
        return textUpdateInterval;
    }

    /**
     * Sets the text update interval, in ticks, of this HologramData.
     *
     * @param textUpdateInterval the new text update interval for the hologram
     */
    public void setTextUpdateInterval(final int textUpdateInterval) {
        this.textUpdateInterval = textUpdateInterval;
    }

    /**
     * Returns the text visibility distance, in blocks/meters, of this HologramData.
     *
     * @return the text visibility distance of the hologram in blocks/meters
     */
    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    /**
     * Sets the text visibility distance, in blocks/meters, of this HologramData.
     *
     * @param visibilityDistance the new visibility distance for the hologram
     */
    public void setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
    }

    /**
     * Returns the linked NPC name of this HologramData.
     *
     * @return the name of the linked NPC, or null if not set
     */
    public @Nullable String getLinkedNpcName() {
        return this.linkedNpcName;
    }

    /**
     * Sets the linked NPC name of this HologramData.
     *
     * @param linkedNpcName the new name for the linked NPC
     */
    public void setLinkedNpcName(@Nullable final String linkedNpcName) {
        this.linkedNpcName = linkedNpcName;
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
