package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.storage.converter.FHConversionRegistry;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.translations.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class FancyHologramsCMD extends Command {

    @NotNull
    private final FancyHolograms plugin;

    public FancyHologramsCMD(@NotNull final FancyHolograms plugin) {
        super("fancyholograms");
        setPermission("fancyholograms.admin");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 1) {
            MessageHelper.info(sender, Constants.FH_COMMAND_USAGE);
            return false;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "save" -> {
                this.plugin.getHologramsManager().saveHolograms();
                MessageHelper.success(sender, "Saved all holograms");
            }
            case "reload" -> {
                this.plugin.getHologramConfiguration().reload(plugin);
                this.plugin.getHologramsManager().reloadHolograms();
                this.plugin.reloadCommands();

                MessageHelper.success(sender, "Reloaded config and holograms");
            }
            case "version" -> {
                FancyHolograms.get().getHologramThread().submit(() -> {
                    FancyHolograms.get().getVersionConfig().checkVersionAndDisplay(sender, false);
                });
            }
            case "convert" -> {
                if (args.length < 2) {
                    MessageHelper.info(sender, "Usage: /fancyholograms convert <type> args[]");
                    return false;
                }

                final String converterId = args[1];
                FHConversionRegistry.getConverterById(converterId)
                    .ifPresentOrElse((converter) -> {
                        final String[] converterArgs = Arrays.asList(args)
                            .subList(1, args.length)
                            .toArray(String[]::new);

                        try {
                            final List<HologramData> holograms = converter.convert(converterArgs);

                            for (final HologramData hologram : holograms) {
                                this.plugin.getHologramsManager().create(hologram);
                            }

                            // TODO(matt): I think exact feedback is needed e.g. WHAT was converted and how (logger?)
                            // TODO(matt): Give options to delete them or teleport and a list of IDs please
                            MessageHelper.info(sender, String.format("Converted successfully, produced %s FancyHolograms!", holograms.size()));
                        } catch (Exception error) {
                            MessageHelper.warning(sender, error.getMessage());
                        }
                    }, () -> MessageHelper.warning(sender, "That converter is not registered. Look at the developer documentation if you are adding converters."));
            }
            default -> {
                MessageHelper.info(sender, Constants.FH_COMMAND_USAGE);
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        return Stream.of("version", "reload", "save", "convert")
            .filter(alias -> alias.startsWith(args[0].toLowerCase(Locale.ROOT)))
            .toList();
    }
}
