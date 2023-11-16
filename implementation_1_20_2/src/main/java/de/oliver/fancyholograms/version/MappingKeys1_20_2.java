package de.oliver.fancyholograms.version;

public enum MappingKeys1_20_2 {

    DATA_INTERPOLATION_DURATION_ID("r"),
    DATA_INTERPOLATION_START_DELTA_TICKS_ID("q"),
    DATA_LINE_WIDTH_ID("aN"),
    DATA_BACKGROUND_COLOR_ID("aO"),
    ;

    private final String mapping;

    MappingKeys1_20_2(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
