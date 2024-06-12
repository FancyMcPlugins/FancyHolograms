package de.oliver.fancyholograms.hologram.version;

public enum MappingKeys1_19_4 {

    DATA_INTERPOLATION_DURATION_ID("r"),
    DATA_INTERPOLATION_START_DELTA_TICKS_ID("q"),
    DATA_LINE_WIDTH_ID("aL"),
    DATA_BACKGROUND_COLOR_ID("aM"),
    ;

    private final String mapping;

    MappingKeys1_19_4(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
