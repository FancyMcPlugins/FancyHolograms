package de.oliver.fancyholograms.converter;

import de.oliver.fancyholograms.converter.impl.DecentHologramsConverter;

public enum Converters {
    DECENT_HOLOGRAMS("DecentHolograms", "2.8.5", DecentHologramsConverter.INSTANCE),
    ;

    private final String pluginName;
    private final String pluginVersion;
    private final HologramConverter converter;

    Converters(String pluginName, String pluginVersion, HologramConverter converter) {
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
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

    public String getPluginVersion() {
        return pluginVersion;
    }

    public HologramConverter getConverter() {
        return converter;
    }
}
