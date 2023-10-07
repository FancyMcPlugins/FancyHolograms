package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class FancyHologramsCMD implements CommandExecutor, TabCompleter {

    @NotNull
    private final FancyHolograms plugin;

    public FancyHologramsCMD(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            MessageHelper.info(sender, "/FancyHolograms <save|reload|version>");
            return false;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "save" -> {
                this.plugin.getHologramsManager().saveHolograms();
                MessageHelper.success(sender, "Saved all holograms");
            }
            case "reload" -> {
                this.plugin.getConfiguration().reload();
                this.plugin.getHologramsManager().reloadHolograms();

                MessageHelper.success(sender, "Reloaded config and holograms");
            }
            case "version" -> FancyHolograms.get().getVersionConfig().checkVersionAndDisplay(sender, false);
            default -> {
                MessageHelper.info(sender, "/FancyHolograms <save|reload|version>");
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        return Stream.of("version", "reload", "save")
                .filter(alias -> alias.startsWith(args[0].toLowerCase(Locale.ROOT)))
                .toList();
    }

}
