package de.oliver.fancyholograms.storage.json.model;

public record JsonLocation(
        String world,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {
}
