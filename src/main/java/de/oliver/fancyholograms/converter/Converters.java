package de.oliver.fancyholograms.converter;

import de.oliver.fancyholograms.converter.impl.DecentHologramsConverter;

public enum Converters {
    DECENT_HOLOGRAMS("DecentHolograms", DecentHologramsConverter.INSTANCE);

    private final String pluginName;
    private final HologramConverter converter;

    Converters(String pluginName, HologramConverter converter) {
        this.pluginName = pluginName;
        this.converter = converter;
    }

    public static Converters getConverter(String name) {
        for (Converters c : values()) {
            if (c.name().equalsIgnoreCase(name) || c.getPluginName().equalsIgnoreCase(name)) {
                return c;
            }
        }

        return null;
    }

    public String getPluginName() {
        return pluginName;
    }

    public HologramConverter getConverter() {
        return converter;
    }
}
