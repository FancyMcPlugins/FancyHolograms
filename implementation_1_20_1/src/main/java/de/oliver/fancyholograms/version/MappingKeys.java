package de.oliver.fancyholograms.version;

public enum MappingKeys {

    DATA_INTERPOLATION_DURATION_ID("q"),
    DATA_INTERPOLATION_START_DELTA_TICKS_ID("p"),
    DATA_LINE_WIDTH_ID("aM"),
    DATA_BACKGROUND_COLOR_ID("aN"),
    ;

    private final String mapping;

    MappingKeys(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
