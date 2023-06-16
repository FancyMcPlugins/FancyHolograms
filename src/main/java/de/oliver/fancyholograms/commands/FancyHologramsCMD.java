package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class FancyHologramsCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        return Stream.of("version", "reload", "save")
                     .filter(alias -> alias.startsWith(args[0].toLowerCase(Locale.ROOT)))
                     .toList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }

        final var plugin = FancyHolograms.getInstance();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "save" -> {
                plugin.getHologramManager().saveHolograms(true);
                MessageHelper.success(sender, "Saved all holograms");
            }
            case "reload" -> {
                plugin.getFancyHologramsConfig().reload();
                plugin.getHologramManager().reloadHolograms();
                MessageHelper.success(sender, "Reloaded config and holograms");
            }
            case "version" -> {
                MessageHelper.info(sender, "<i>Checking version, please wait...</i>");

                final var fetcher = plugin.getVersionFetcher();
                final var current = new ComparableVersion(plugin.getDescription().getVersion());

                CompletableFuture.supplyAsync(fetcher::getNewestVersion).whenComplete((newest, error) -> {
                    if (newest == null || error != null) {
                        MessageHelper.error(sender, "Could not find latest version");
                    } else if (newest.compareTo(current) > 0) {
                        MessageHelper.warning(sender, """
                                                      You are using an outdated version of the FancyHolograms Plugin
                                                      [!] Please download the newest version (%s): <click:open_url:'%s'><u>click here</u></click>
                                                      """.formatted(newest, fetcher.getDownloadUrl()));
                    } else {
                        MessageHelper.success(sender, "You are using the latest version of the FancyHolograms Plugin (" + current + ")");
                    }
                });
            }
            default -> {
                return false;
            }
        }

        return true;
    }
}
