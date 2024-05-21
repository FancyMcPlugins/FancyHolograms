package de.oliver.fancyholograms.version;

public enum MappingKeys1_20_6 {

    DISPLAY__DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID("DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID"),
    DISPLAY__DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID("DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID"),
    TEXT_DISPLAY__DATA_LINE_WIDTH_ID("DATA_LINE_WIDTH_ID"),
    TEXT_DISPLAY__DATA_BACKGROUND_COLOR_ID("DATA_BACKGROUND_COLOR_ID"),
    ;

    private final String mapping;

    MappingKeys1_20_6(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
