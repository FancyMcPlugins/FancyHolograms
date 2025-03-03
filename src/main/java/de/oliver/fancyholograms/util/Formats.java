package de.oliver.fancyholograms.util;

import java.text.DecimalFormat;

public class Formats {

    public static final DecimalFormat DECIMAL = new DecimalFormat("#########.##");
    public final static DecimalFormat COORDINATES_DECIMAL = new DecimalFormat("#########.##");

    private Formats() {
        throw new IllegalStateException("Utility class");
    }
}
