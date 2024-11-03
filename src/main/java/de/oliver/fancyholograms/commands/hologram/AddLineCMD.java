package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AddLineCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(player.hasPermission("fancyholograms.hologram.edit.line.add"))) {
            MessageHelper.error(player, "You don't have the required permission to add a line to this hologram");
            return true;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        String text = "";
        for (int i = 3; i < args.length; i++) {
            text += args[i] + " ";
        }
        text = text.substring(0, text.length() - 1);

        return SetLineCMD.setLine(player, hologram, Integer.MAX_VALUE, text);
    }
}
