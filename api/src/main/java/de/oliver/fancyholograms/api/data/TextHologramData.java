package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.Hologram;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextHologramData implements Data {

    public static final TextDisplay.TextAlignment DEFAULT_TEXT_ALIGNMENT = TextDisplay.TextAlignment.CENTER;
    public static final boolean DEFAULT_TEXT_SHADOW_STATE = false;
    public static final int DEFAULT_TEXT_UPDATE_INTERVAL = -1;

    private List<String> text;
    private TextColor background;
    private TextDisplay.TextAlignment textAlignment;
    private boolean textShadow;
    private int textUpdateInterval;

    public TextHologramData(List<String> text, TextColor background, TextDisplay.TextAlignment textAlignment, boolean textShadow, int textUpdateInterval) {
        this.text = text;
        this.background = background;
        this.textAlignment = textAlignment;
        this.textShadow = textShadow;
        this.textUpdateInterval = textUpdateInterval;
    }

    public TextHologramData() {
    }

    public static TextHologramData getDefault(String name) {
        List<String> text = new ArrayList<>();
        text.add("Edit this line with /hologram edit " + name);
        
        return new TextHologramData(
                text,
                null,
                DEFAULT_TEXT_ALIGNMENT,
                DEFAULT_TEXT_SHADOW_STATE,
                DEFAULT_TEXT_UPDATE_INTERVAL
        );
    }

    @Override
    public void read(ConfigurationSection section, String name) {
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
        section.set("text", text);
        section.set("text_shadow", textShadow);
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

    public boolean isTextShadow() {
        return textShadow;
    }

    public TextHologramData setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
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
    public Data copy() {
        return new TextHologramData(
                new ArrayList<>(text),
                background,
                textAlignment,
                textShadow,
                textUpdateInterval
        );
    }
}
