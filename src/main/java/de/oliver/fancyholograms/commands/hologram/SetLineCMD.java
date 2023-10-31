package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetLineCMD implements Subcommand {

    public static boolean setLine(Player player, Hologram hologram, int index, String text) {
        if (!(hologram.getData().getTypeData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        final var lines = new ArrayList<>(textData.getText());

        if (index >= lines.size()) {
            lines.add(text == null ? " " : text);
        } else if (text == null) {
            lines.remove(index);
        } else {
            lines.set(index, text);
        }

        final var copied = hologram.getData().copy();
        ((TextHologramData) copied.getTypeData()).setText(lines);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        textData.setText(((TextHologramData) copied.getTypeData()).getText());

        MessageHelper.success(player, "Changed text for line " + (Math.min(index, lines.size() - 1) + 1));
        return true;
    }

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        var index = Ints.tryParse(args[3]);
        if (index == null) {
            MessageHelper.error(player, "Could not parse line number");
            return false;
        }

        if (index < 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        index--;

        String text = "";
        for (int i = 4; i < args.length; i++) {
            text += args[i] + " ";
        }
        text = text.substring(0, text.length() - 1);

        return setLine(player, hologram, index, text);
    }
}
