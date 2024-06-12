package de.oliver.fancyholograms.hologram.version;

public enum MappingKeys1_20_4 {

    DISPLAY__DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID("r"),
    DISPLAY__DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID("q"),
    TEXT_DISPLAY__DATA_LINE_WIDTH_ID("aN"),
    TEXT_DISPLAY__DATA_BACKGROUND_COLOR_ID("aO"),
    ;

    private final String mapping;

    MappingKeys1_20_4(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
