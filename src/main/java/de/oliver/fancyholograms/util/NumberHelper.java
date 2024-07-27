package de.oliver.fancyholograms.util;

import java.util.Optional;

public class NumberHelper {

    public static Optional<Integer> parseInt(String toParse) {
        try {
            return Optional.of(Integer.parseInt(toParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
