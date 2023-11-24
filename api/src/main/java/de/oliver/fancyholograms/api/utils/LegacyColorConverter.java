package de.oliver.fancyholograms.api.utils;

public class LegacyColorConverter {

    public static String legacyColorCodesToMiniMessages(String s) {
        if (s == null) {
            return null;
        }

        s = s.replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&0", "<black>")
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&r", "<reset>")
                .replaceAll("&l", "<bold>")
                .replaceAll("&o", "<italic>")
                .replaceAll("&n", "<underlined>")
                .replaceAll("&m", "<strikethrough>")
                .replaceAll("&k", "<obfuscated>")
        ;

        return s;
    }

}
