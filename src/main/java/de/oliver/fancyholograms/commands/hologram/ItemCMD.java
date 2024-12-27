package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCMD implements Subcommand {
    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(sender.hasPermission("fancyholograms.hologram.edit.item"))) {
            MessageHelper.error(sender, "You don't have the required permission to edit a hologram");
            return false;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }

        if (!(hologram.getData() instanceof ItemHologramData itemData)) {
            MessageHelper.error(player, "This command can only be used on item holograms");
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || item.getAmount() < 1) {
            MessageHelper.error(player, "You need to hold an item in your hand");
            return false;
        }


        if (item == itemData.getItemStack()) {
            MessageHelper.warning(player, "This item is already set");
            return false;
        }

        final var copied = itemData.copy(itemData.getName());
        copied.setItemStack(item);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (copied.getItemStack() == itemData.getItemStack()) {
            MessageHelper.warning(player, "This item is already set");
            return false;
        }

        itemData.setItemStack(item);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Set the item to '" + item.getType().name() + "'");
        return true;
    }
}
