package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextHologramData extends DisplayHologramData {

    public static final TextDisplay.TextAlignment DEFAULT_TEXT_ALIGNMENT = TextDisplay.TextAlignment.CENTER;
    public static final boolean DEFAULT_TEXT_SHADOW_STATE = false;
    public static final boolean DEFAULT_SEE_THROUGH = false;
    public static final int DEFAULT_TEXT_UPDATE_INTERVAL = -1;

    private List<String> text;
    private TextColor background;
    private TextDisplay.TextAlignment textAlignment;
    private boolean textShadow;
    private boolean seeThrough;
    private int textUpdateInterval;

    public TextHologramData(String name, Location location) {
        super(name, HologramType.TEXT, location);
    }

    public List<String> getText() {
        return text;
    }

    public TextHologramData setText(List<String> text) {
        this.text = text;
        return this;
    }

    public void addLine(String line) {
        text.add(line);
    }

    public void removeLine(int index) {
        text.remove(index);
    }

    public TextColor getBackground() {
        return background;
    }

    public TextHologramData setBackground(TextColor background) {
        this.background = background;
        return this;
    }

    public TextDisplay.TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public TextHologramData setTextAlignment(TextDisplay.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public boolean hasTextShadow() {
        return textShadow;
    }

    public TextHologramData setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public TextHologramData setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public int getTextUpdateInterval() {
        return textUpdateInterval;
    }

    public TextHologramData setTextUpdateInterval(int textUpdateInterval) {
        this.textUpdateInterval = textUpdateInterval;
        return this;
    }

    @Override
    public void read(ConfigurationSection section, String name) {
        super.read(section, name);
        text = section.getStringList("text");
        if (text.isEmpty()) {
            text = List.of("Could not load hologram text");
        }

        textShadow = section.getBoolean("text_shadow", false);
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
                background = TextColor.fromHexString(backgroundStr);
            } else {
                background = NamedTextColor.NAMES.value(backgroundStr.toLowerCase(Locale.ROOT).trim().replace(' ', '_'));
            }
        }
    }

    @Override
    public void write(ConfigurationSection section, String name) {
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
        } else if (background instanceof NamedTextColor named) {
            color = named.toString();
        } else {
            color = background.asHexString();
        }

        section.set("background", color);
    }

    public static TextHologramData getDefault(String name, Location location) {
        TextHologramData textHologramData = new TextHologramData(name, location);
        textHologramData
            .setText(new ArrayList<>(List.of("Edit this line with /hologram edit " + name)))
            .setTextAlignment(DEFAULT_TEXT_ALIGNMENT)
            .setTextShadow(DEFAULT_TEXT_SHADOW_STATE)
            .setSeeThrough(DEFAULT_SEE_THROUGH)
            .setTextUpdateInterval(DEFAULT_TEXT_UPDATE_INTERVAL)
            .setScale(DEFAULT_SCALE)
            .setShadowRadius(DEFAULT_SHADOW_RADIUS)
            .setShadowStrength(DEFAULT_SHADOW_STRENGTH)
            .setBillboard(DEFAULT_BILLBOARD)
            .setVisibilityDistance(DEFAULT_VISIBILITY_DISTANCE)
            .setVisibleByDefault(DEFAULT_IS_VISIBLE);

        return textHologramData;
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
            .setVisibilityDistance(getVisibilityDistance())
            .setVisibleByDefault(isVisibleByDefault())
            .setLinkedNpcName(getLinkedNpcName());

        return textHologramData;
    }
}
