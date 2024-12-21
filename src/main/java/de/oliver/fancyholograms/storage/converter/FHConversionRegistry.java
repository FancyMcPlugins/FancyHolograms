package de.oliver.fancyholograms.storage.converter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FHConversionRegistry {
    private static final Map<String, HologramConverter> converters = new HashMap<>();

    public static void registerBuiltInConverters() {
        register("DecentHolograms", new DecentHologramsConverter());
    }

    public static boolean register(@NotNull String id, HologramConverter converter) {
        return converters.putIfAbsent(id, converter) != null;
    }

    public static @NotNull Optional<HologramConverter> getConverterById(@NotNull String id) {
        return Optional.ofNullable(converters.get(id));
    }

    public static <T extends HologramConverter> @NotNull Optional<T> getConverter(@NotNull String id) {
        return getConverterById(id)
            .map((converter) -> {
                try {
                    return (T) converter;
                } catch (ClassCastException ignored) {
                    return null;
                }
            });
    }

    public static <T extends HologramConverter> @NotNull Optional<T> getConverter(@NotNull Class<T> clazz) {
        return converters.values()
            .stream()
            .filter(clazz::isInstance)
            .findFirst()
            .map((converter) -> {
                try {
                    return (T) converter;
                } catch (ClassCastException ignored) {
                    return null;
                }
            });
    }

    public static @NotNull Set<String> getAllConverterIds() {
        return converters.keySet();
    }
}
