package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TextHologramData extends DisplayHologramData {

    public static final List<String> DEFAULT_TEXT = List.of("Edit this line with /hologram edit <name>");
    public static final TextDisplay.TextAlignment DEFAULT_TEXT_ALIGNMENT = TextDisplay.TextAlignment.CENTER;
    public static final boolean DEFAULT_TEXT_SHADOW_STATE = false;
    public static final boolean DEFAULT_SEE_THROUGH = false;
    public static final int DEFAULT_TEXT_UPDATE_INTERVAL = -1;

    private List<String> text = new ArrayList<>(DEFAULT_TEXT);
    private Color background = null;
    private TextDisplay.TextAlignment textAlignment = DEFAULT_TEXT_ALIGNMENT;
    private boolean textShadow = DEFAULT_TEXT_SHADOW_STATE;
    private boolean seeThrough = DEFAULT_SEE_THROUGH;
    private int textUpdateInterval = DEFAULT_TEXT_UPDATE_INTERVAL;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public TextHologramData(String name, Location location) {
        super(name, HologramType.TEXT, location);
    }

    public List<String> getText() {
        return text;
    }

    public TextHologramData setText(List<String> text) {
        if (!Objects.equals(this.text, text)) {
            this.text = text;
            setHasChanges(true);
        }

        return this;
    }

    public TextHologramData addLine(String line) {
        text.add(line);
        setHasChanges(true);
        return this;
    }

    public TextHologramData removeLine(int index) {
        text.remove(index);
        setHasChanges(true);
        return this;
    }

    public TextHologramData setLine(int index, String line) {
        text.set(index, line);
        setHasChanges(true);
        return this;
    }

    public TextHologramData clearText() {
        text.clear();
        setHasChanges(true);
        return this;
    }

    public Color getBackground() {
        return background;
    }

    public TextHologramData setBackground(Color background) {
        if (!Objects.equals(this.background, background)) {
            this.background = background;
            setHasChanges(true);
        }

        return this;
    }

    public TextDisplay.TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public TextHologramData setTextAlignment(TextDisplay.TextAlignment textAlignment) {
        if (!Objects.equals(this.textAlignment, textAlignment)) {
            this.textAlignment = textAlignment;
            setHasChanges(true);
        }

        return this;
    }

    public boolean hasTextShadow() {
        return textShadow;
    }

    public TextHologramData setTextShadow(boolean textShadow) {
        if (this.textShadow != textShadow) {
            this.textShadow = textShadow;
            setHasChanges(true);
        }

        return this;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public TextHologramData setSeeThrough(boolean seeThrough) {
        if (this.seeThrough != seeThrough) {
            this.seeThrough = seeThrough;
            setHasChanges(true);
        }

        return this;
    }

    public int getTextUpdateInterval() {
        return textUpdateInterval;
    }

    public TextHologramData setTextUpdateInterval(int textUpdateInterval) {
        if (this.textUpdateInterval != textUpdateInterval) {
            this.textUpdateInterval = textUpdateInterval;
            setHasChanges(true);
        }

        return this;
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        text = section.getStringList("text");
        if (text.isEmpty()) {
            text = List.of("Could not load hologram text");
            //TODO: maybe return false here?
        }

        textShadow = section.getBoolean("text_shadow", DEFAULT_TEXT_SHADOW_STATE);
        seeThrough = section.getBoolean("see_through", DEFAULT_SEE_THROUGH);
        textUpdateInterval = section.getInt("update_text_interval", DEFAULT_TEXT_UPDATE_INTERVAL);

        String textAlignmentStr = section.getString("text_alignment", DEFAULT_TEXT_ALIGNMENT.name().toLowerCase());
        textAlignment = switch (textAlignmentStr.toLowerCase(Locale.ROOT)) {
            case "right" -> TextDisplay.TextAlignment.RIGHT;
            case "left" -> TextDisplay.TextAlignment.LEFT;
            default -> TextDisplay.TextAlignment.CENTER;
        };

        background = null;
        String backgroundStr = section.getString("background", null);
        if (backgroundStr != null) {
            if (backgroundStr.equalsIgnoreCase("transparent")) {
                background = Hologram.TRANSPARENT;
            } else if (backgroundStr.startsWith("#")) {
                background = Color.fromARGB((int) Long.parseLong(backgroundStr.substring(1), 16));
                //backwards compatibility, make rgb hex colors solid color -their alpha is 0 by default-
                if (backgroundStr.length() == 7) background = background.setAlpha(255);
            } else {
                background = Color.fromARGB(NamedTextColor.NAMES.value(backgroundStr.toLowerCase(Locale.ROOT).trim().replace(' ', '_')).value() | 0xC8000000);
            }
        }

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("text", text);
        section.set("text_shadow", textShadow);
        section.set("see_through", seeThrough);
        section.set("text_alignment", textAlignment.name().toLowerCase(Locale.ROOT));
        section.set("update_text_interval", textUpdateInterval);

        final String color;
        if (background == null) {
            color = null;
        } else if (background == Hologram.TRANSPARENT) {
            color = "transparent";
        } else {
            NamedTextColor named = background.getAlpha() == 255 ? NamedTextColor.namedColor(background.asRGB()) : null;
            color = named != null ? named.toString() : '#' + Integer.toHexString(background.asARGB());
        }

        section.set("background", color);

        return true;
    }

    @Override
    public TextHologramData copy(String name) {
        TextHologramData textHologramData = new TextHologramData(name, getLocation());
        textHologramData
                .setText(this.getText())
                .setBackground(this.getBackground())
                .setTextAlignment(this.getTextAlignment())
                .setTextShadow(this.hasTextShadow())
                .setSeeThrough(this.isSeeThrough())
                .setTextUpdateInterval(this.getTextUpdateInterval())
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

        return textHologramData;
    }
}
