package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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
                this.plugin.getHologramsManager().saveHolograms(true);
                MessageHelper.success(sender, "Saved all holograms");
            }
            case "reload" -> {
                this.plugin.getConfiguration().reload();
                this.plugin.getHologramsManager().reloadHolograms();

                MessageHelper.success(sender, "Reloaded config and holograms");
            }
            case "version" -> {
                MessageHelper.info(sender, "<i>Checking version, please wait...</i>");

                final var fetcher = this.plugin.getVersionFetcher();
                final var current = new ComparableVersion(this.plugin.getDescription().getVersion());

                supplyAsync(fetcher::getNewestVersion).whenComplete((newest, error) -> {
                    if (newest == null || error != null) {
                        MessageHelper.error(sender, "Could not find latest version");
                    } else if (newest.compareTo(current) > 0) {
                        MessageHelper.warning(sender, """
                                You are using an outdated version of the FancyHolograms Plugin (%s)
                                [!] Please download the newest version (%s): <click:open_url:'%s'><u>click here</u></click>
                                """.formatted(current, newest, fetcher.getDownloadUrl()));
                    } else {
                        MessageHelper.success(sender, "You are using the latest version of the FancyHolograms Plugin (" + current + ")");
                    }
                });
            }
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
