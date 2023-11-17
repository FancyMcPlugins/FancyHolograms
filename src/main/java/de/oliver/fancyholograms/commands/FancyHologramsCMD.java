package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.converter.Converters;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        if (args.length == 0) {
            MessageHelper.info(sender, "/FancyHolograms version - checks for a new version of FancyHolograms");
            MessageHelper.info(sender, "/FancyHolograms save - saves all holograms to disk");
            MessageHelper.info(sender, "/FancyHolograms reload - reloads the config and holograms");
            MessageHelper.info(sender, "/FancyHolograms convert <other hologram plugin> - converts all holograms from the one plugin and DELETES them afterwards");
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
            case "convert" -> {
                if (args.length < 2) {
                    MessageHelper.info(sender, "Wrong usage, type '/FancyHologram' for help");
                    return false;
                }

                Converters converter = Converters.getConverter(args[1]);
                if (converter == null) {
                    MessageHelper.error(sender, "Could not find converter for the " + args[1] + " plugin");
                    return false;
                }

                if (!Bukkit.getPluginManager().isPluginEnabled(converter.getPluginName())) {
                    MessageHelper.error(sender, "The " + converter.getPluginName() + " plugin is not enabled");
                    return false;
                }

                final String pluginVersion = Bukkit.getPluginManager().getPlugin(converter.getPluginName()).getDescription().getVersion();
                if (!pluginVersion.equals(converter.getPluginVersion())) {
                    MessageHelper.error(sender, "Please get " + converter.getPluginName() + " version <bold>" + converter.getPluginVersion());
                    return false;
                }

                List<HologramData> hologramData = converter.getConverter().convertAll();
                for (HologramData data : hologramData) {
                    MessageHelper.info(sender, "Successfully converted the " + data.getName() + " hologram to a fancy-hologram");
                }

                MessageHelper.success(sender, "Converted all holograms from " + converter.getPluginName() + " to FancyHolograms!");
            }
            default -> {
                MessageHelper.info(sender, "Wrong usage, type '/FancyHologram' for help");
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("version", "reload", "save", "convert")
                    .filter(alias -> alias.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .toList();
        } else if (args.length == 2 && args[0].equalsIgnoreCase("convert")) {
            return Arrays.stream(Converters.values())
                    .map((c) -> c.name().toLowerCase())
                    .toList();
        }

        return Collections.emptyList();
    }

}
