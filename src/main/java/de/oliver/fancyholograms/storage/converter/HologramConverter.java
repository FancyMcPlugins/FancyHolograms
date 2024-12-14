package de.oliver.fancyholograms.storage.converter;

import de.oliver.fancyholograms.api.data.HologramData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HologramConverter {

    boolean canRunConverter();

    /**
     * Returns a list of converted holograms
     * @param args Arguments provided in the command invocation.
     * @return A list of converted holograms.
     */
    List<HologramData> convert(@NotNull String[] args);

}
