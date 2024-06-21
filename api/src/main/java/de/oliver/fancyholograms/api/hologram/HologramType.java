package de.oliver.fancyholograms.api.hologram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum HologramType {
    TEXT(Arrays.asList("background", "textshadow", "textalignment", "seethrough", "setline", "removeline", "addline", "insertbefore", "insertafter", "updatetextinterval")),
    ITEM(List.of("item")),
    BLOCK(List.of("block")),
    HOLOGRAM_STACK(Collections.emptyList());

    private final List<String> commands;

    HologramType(List<String> commands) {
        this.commands = commands;
    }

    public static HologramType getByName(String name) {
        for (HologramType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    public List<String> getCommands() {
        return commands;
    }

}
