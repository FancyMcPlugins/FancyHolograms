package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCMD implements Subcommand {
    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(hologram.getData().getTypeData() instanceof ItemHologramData itemData)) {
            MessageHelper.error(player, "This command can only be used on item holograms");
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || item.getAmount() < 1) {
            MessageHelper.error(player, "You need to hold an item in your hand");
            return false;
        }


        if (item == itemData.getItem()) {
            MessageHelper.warning(player, "This item is already set");
            return false;
        }

        HologramData copied = hologram.getData().copy();
        ((ItemHologramData) copied.getTypeData()).setItem(item);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (((ItemHologramData) copied.getTypeData()).getItem() == itemData.getItem()) {
            MessageHelper.warning(player, "This item is already set");
            return false;
        }

        itemData.setItem(item);

        MessageHelper.success(player, "Set the item to '" + item.getType().name() + "'");
        return true;
    }
}
