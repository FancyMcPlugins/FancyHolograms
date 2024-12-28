package de.oliver.fancyholograms.storage.json.model;

public record JsonDataUnion(
        JsonHologramData hologram_data,
        JsonDisplayHologramData display_hologram_data,
        JsonTextHologramData text_hologram_data
) {
}
