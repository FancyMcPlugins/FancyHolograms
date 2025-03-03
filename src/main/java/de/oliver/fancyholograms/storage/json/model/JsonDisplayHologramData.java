package de.oliver.fancyholograms.storage.json.model;

import org.bukkit.entity.Display;

public record JsonDisplayHologramData(
    JsonVec3f scale,
    JsonVec3f translation,
    float shadow_radius,
    float shadow_strength,
    JsonBrightness brightness,
    Display.Billboard billboard
) {
}

