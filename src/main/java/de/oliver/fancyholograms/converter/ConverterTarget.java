package de.oliver.fancyholograms.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ConverterTarget {
    private static final ConverterTarget ALL = new ConverterTarget(Pattern.compile(".*"));
    private final @NotNull Pattern hologramIdRegex;

    public ConverterTarget(@NotNull Pattern matching) {
        this.hologramIdRegex = matching;
    }

    public static @NotNull ConverterTarget all() {
        return ALL;
    }

    public static @NotNull ConverterTarget ofAll(@NotNull String first, @NotNull String... others) {
        StringBuilder builder = new StringBuilder(first);

        if (others.length > 0) {
            builder.append("|");
        }

        builder.append(String.join("|", others));

        return new ConverterTarget(Pattern.compile(builder.toString()));
    }

    public static @NotNull ConverterTarget ofSingle(@NotNull String match) {
        return new ConverterTarget(Pattern.compile(match));
    }

    public static @Nullable ConverterTarget ofStringNullable(@NotNull String match) {

        if (match.equalsIgnoreCase("*")) {
            return all();
        }

        try {
            return new ConverterTarget(Pattern.compile(match));
        } catch (Exception ignored) {
            return null;
        }
    }

    public @NotNull Pattern getRegex() {
        return hologramIdRegex;
    }

    public boolean matches(@NotNull String hologramId) {
        return hologramIdRegex.asMatchPredicate().test(hologramId);
    }

}
