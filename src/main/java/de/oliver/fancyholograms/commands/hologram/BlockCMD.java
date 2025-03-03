package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.block"))) {
            MessageHelper.error(player, "You don't have the required permission to change the block of this hologram");
            return false;
        }

        if (!(hologram.getData() instanceof BlockHologramData blockData)) {
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

        final var copied = blockData.copy(blockData.getName());
        copied.setBlock(block);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (copied.getBlock() == blockData.getBlock()) {
            MessageHelper.warning(player, "This block is already set");
            return false;
        }

        blockData.setBlock(block);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Set block to '" + block.name() + "'");
        return true;
    }
}
