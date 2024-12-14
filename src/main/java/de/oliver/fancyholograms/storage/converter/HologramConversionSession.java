package de.oliver.fancyholograms.storage.converter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HologramConversionSession {

    private final @NotNull HologramConverter converter;
    private final @NotNull List<ConvertedStackedHologram> results = new ArrayList<>();

    public HologramConversionSession(@NotNull HologramConverter converter) {
        this.converter = converter;
    }

    public void run() {

    }
}
