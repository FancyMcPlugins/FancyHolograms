package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LinkWithNpcCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!FancyHolograms.isUsingFancyNpcs()) {
            MessageHelper.warning(player, "You need to install the FancyNpcs plugin for this functionality to work");
            MessageHelper.warning(player, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
            return false;
        }

        String name = args[3];

        if (hologram.getData().getLinkedNpcName() != null) {
            MessageHelper.error(player, "This hologram is already linked with an NPC");
            return false;
        }

        final var npc = FancyNpcsPlugin.get().getNpcManager().getNpc(name);
        if (npc == null) {
            MessageHelper.error(player, "Could not find NPC with that name");
            return false;
        }

        hologram.getData().setLinkedNpcName(npc.getData().getName());

        FancyHolograms.get().getHologramsManager().syncHologramWithNpc(hologram);

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Linked hologram with NPC");
        return true;
    }
}
