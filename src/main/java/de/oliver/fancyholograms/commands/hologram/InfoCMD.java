package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfoCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        DisplayHologramData displayData = hologram.getData().getDisplayData();

        MessageHelper.info(player, "<b>Information about the " + hologram.getData().getName() + " hologram:");
        MessageHelper.info(player, "Name: <gray>" + hologram.getData().getName());
        MessageHelper.info(player, "Type: <gray>" + hologram.getData().getType().name());
        MessageHelper.info(player, "Location: <gray>" + displayData.getLocation().getWorld().getName() + " " + displayData.getLocation().getX() + " / " + displayData.getLocation().getY() + " / " + displayData.getLocation().getZ());
        MessageHelper.info(player, "Scale: <gray>x" + displayData.getScale().x());
        MessageHelper.info(player, "Visibility distance: <gray>" + displayData.getVisibilityDistance() + " blocks");
        MessageHelper.info(player, "Billboard: <gray>" + displayData.getBillboard().name());
        MessageHelper.info(player, "Shadow radius: <gray>" + displayData.getShadowRadius());
        MessageHelper.info(player, "Shadow strength: <gray>" + displayData.getShadowStrength());
        if (displayData.getLinkedNpcName() != null) {
            MessageHelper.info(player, "Linked npc: <gray>" + displayData.getLinkedNpcName());
        }

        if (hologram.getData().getTypeData() instanceof TextHologramData textData) {
            MessageHelper.info(player, "Text: ");
            for (String line : textData.getText()) {
                MessageHelper.info(player, " <reset> " + line);
            }

            if (textData.getBackground() != null) {
                MessageHelper.info(player, "Background: <gray>" + textData.getBackground().asHexString());
            } else {
                MessageHelper.info(player, "Background: <gray>default");
            }

            MessageHelper.info(player, "Text alignment: <gray>" + textData.getTextAlignment().name());
            MessageHelper.info(player, "Text shadow: <gray>" + (textData.isTextShadow() ? "enabled" : "disabled"));
            if (textData.getTextUpdateInterval() == -1) {
                MessageHelper.info(player, "Update text interval: <gray>not updating");
            } else {
                MessageHelper.info(player, "Update text interval: <gray>" + textData.getTextUpdateInterval() + " ticks");
            }
        } else if (hologram.getData().getTypeData() instanceof BlockHologramData blockData) {
            MessageHelper.info(player, "Block: <gray>" + blockData.getBlock().name());
        } else if (hologram.getData().getTypeData() instanceof ItemHologramData itemData) {
            MessageHelper.info(player, "Item: <gray>" + itemData.getItem().getType().name());
        }

        return true;
    }
}
