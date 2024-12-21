package de.oliver.fancyholograms.storage.converter;

import de.oliver.fancyholograms.api.data.HologramData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HologramConverter {

    boolean canRunConverter();

    /**
     * Returns a list of converted holograms
     * @param spec Configuration of the hologram conversion
     * @return A list of converted holograms.
     */
    @NotNull List<HologramData> convert(@NotNull HologramConversionSession spec);

    default @NotNull List<String> getConvertableHolograms() {
        return List.of();
    }

}
