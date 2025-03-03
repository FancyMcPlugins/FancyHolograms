package de.oliver.fancyholograms.storage.json.model;

import de.oliver.fancyholograms.api.data.property.Visibility;
import de.oliver.fancyholograms.api.hologram.HologramType;

public record JsonHologramData(
        String name,
        HologramType type,
        JsonLocation location,
        int visibilityDistance,
        Visibility visibility,
        String linkedNpcName
) {
}

