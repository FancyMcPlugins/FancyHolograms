package de.oliver.fancyholograms.version;

public enum MappingKeys {

    DATA_INTERPOLATION_DURATION_ID("r"),
    DATA_INTERPOLATION_START_DELTA_TICKS_ID("q"),
    DATA_LINE_WIDTH_ID("aL"),
    DATA_BACKGROUND_COLOR_ID("aM"),
    ;

    private final String mapping;

    MappingKeys(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
