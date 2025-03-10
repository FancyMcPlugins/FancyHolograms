package de.oliver.fancyholograms.storage.json.model;

import org.bukkit.entity.TextDisplay;

import java.util.List;

public record JsonTextHologramData(
        List<String> text,
        boolean text_shadow,
        boolean see_through,
        TextDisplay.TextAlignment text_alignment,
        int text_update_interval,
        String background_color
) {
}
