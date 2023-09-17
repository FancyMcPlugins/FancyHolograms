package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlinkWithNpcCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!FancyHolograms.isUsingFancyNpcs()) {
            MessageHelper.warning(player, "You need to install the FancyNpcs plugin for this functionality to work");
            MessageHelper.warning(player, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
            return false;
        }

        if (hologram.getData().getLinkedNpcName() == null) {
            MessageHelper.error(player, "This hologram is not linked with an NPC");
            return false;
        }

        final var npc = FancyNpcsPlugin.get().getNpcManager().getNpc(hologram.getData().getLinkedNpcName());

        hologram.getData().setLinkedNpcName(null);

        if (npc != null) {
            npc.getData().setDisplayName(npc.getData().getName());
            npc.updateForAll();
        }

        MessageHelper.success(player, "Unlinked hologram with NPC");
        return true;
    }
}
