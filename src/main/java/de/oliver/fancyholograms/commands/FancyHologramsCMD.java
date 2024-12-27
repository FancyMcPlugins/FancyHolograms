package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.converter.ConverterTarget;
import de.oliver.fancyholograms.converter.FHConversionRegistry;
import de.oliver.fancyholograms.converter.HologramConversionSession;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class FancyHologramsCMD extends Command {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public FancyHologramsCMD(@NotNull final FancyHologramsPlugin plugin) {
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
                this.plugin.savePersistentHolograms();
                MessageHelper.success(sender, "Saved all holograms");
            }
            case "reload" -> {
                this.plugin.getHologramConfiguration().reload(plugin);

                this.plugin.getRegistry().clear();
                for (World world : Bukkit.getWorlds()) {
                    Collection<HologramData> hologramData = this.plugin.getStorage().loadAll(world.getName());
                    for (HologramData data : hologramData) {
                        Hologram hologram = this.plugin.getHologramFactory().apply(data);
                        this.plugin.getRegistry().register(hologram);
                    }
                }

                MessageHelper.success(sender, "Reloaded config and holograms");
            }
            case "version" -> {
                FancyHologramsPlugin.get().getHologramThread().submit(() -> {
                    FancyHologramsPlugin.get().getVersionConfig().checkVersionAndDisplay(sender, false);
                });
            }
            case "convert" -> {
                if (args.length < 3) {
                    MessageHelper.info(sender, "Usage: /fancyholograms convert <type> <targets> [args...]");
                    return false;
                }

                final String converterId = args[1];
                FHConversionRegistry.getConverterById(converterId)
                    .ifPresentOrElse((converter) -> {
                        final String[] converterArgs = Arrays.asList(args)
                            .subList(2, args.length)
                            .toArray(String[]::new);

                        final ConverterTarget target = ConverterTarget.ofStringNullable(args[2]);

                        if (target == null) {
                            MessageHelper.error(sender, "Invalid regex for your conversion target!");
                            return;
                        }

                        final HologramConversionSession session = new HologramConversionSession(target, sender, converterArgs);

                        try {
                            final List<HologramData> holograms = converter.convert(session);

                            for (final HologramData data : holograms) {
                                final Hologram hologram = this.plugin.getHologramFactory().apply(data);
                                this.plugin.getRegistry().register(hologram);
                            }

                            this.plugin.savePersistentHolograms();
                            // TODO(matt): Give options to delete them or teleport and a list of IDs please

                            MessageHelper.success(sender, String.format("Converted successfully, produced %s total holograms!", holograms.size()));
                        } catch (Exception error) {
                            MessageHelper.error(sender, error.getMessage());
                        }
                    }, () -> MessageHelper.error(sender, "That converter is not registered. Look at the developer documentation if you are adding converters."));
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
        if (args.length < 1) {
            return Collections.emptyList();
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("version", "reload", "save", "convert"));
        } else {
            if (Objects.equals(args[0], "convert")) {

                if (args.length == 2) {
                    suggestions.addAll(FHConversionRegistry.getAllUsableConverterIds());
                } else if (args.length == 3) {
                    final String converterId = args[1];
                    FHConversionRegistry.getConverterById(converterId)
                        .ifPresent((converter) -> {
                            suggestions.addAll(converter.getConvertableHolograms());
                            suggestions.add("*");
                        });
                }
            }
        }

        String lastArgument = args[args.length - 1];

        return suggestions.stream()
            .filter(alias -> alias.startsWith(lastArgument.toLowerCase(Locale.ROOT)))
            .toList();
    }
}
