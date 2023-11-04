package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(hologram.getData().getTypeData() instanceof BlockHologramData blockData)) {
            MessageHelper.error(player, "This command can only be used on item holograms");
            return false;
        }

        Material block = Material.getMaterial(args[3]);
        if (block == null) {
            MessageHelper.error(player, "Could not find block type");
            return false;
        }

        if (block == blockData.getBlock()) {
            MessageHelper.warning(player, "This block is already set");
            return false;
        }

        HologramData copied = hologram.getData().copy();
        ((BlockHologramData) copied.getTypeData()).setBlock(block);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (((BlockHologramData) copied.getTypeData()).getBlock() == blockData.getBlock()) {
            MessageHelper.warning(player, "This block is already set");
            return false;
        }

        blockData.setBlock(block);

        MessageHelper.success(player, "Set block to '" + block.name() + "'");
        return true;
    }
}
