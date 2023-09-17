package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Subcommand {

    List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args);

    boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args);

}
