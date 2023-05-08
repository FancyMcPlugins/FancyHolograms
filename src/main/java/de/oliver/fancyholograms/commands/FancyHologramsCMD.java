package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class FancyHologramsCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Stream.of("version", "reload", "save").filter(input -> input.toLowerCase().startsWith(args[0])).toList();
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1 && args[0].equalsIgnoreCase("version")){
            MessageHelper.info(sender, "<i>Checking version, please wait...</i>");
            new Thread(() -> {
                ComparableVersion newestVersion = FancyHolograms.getInstance().getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if(newestVersion == null){
                    MessageHelper.error(sender, "Could not find latest version");
                } else if(newestVersion.compareTo(currentVersion) > 0){
                    MessageHelper.warning(sender, "You are using an outdated version of the FancyHolograms Plugin");
                    MessageHelper.warning(sender, "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyHolograms.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
                } else {
                    MessageHelper.success(sender, "You are using the latest version of the FancyHolograms Plugin (" + currentVersion + ")");
                }
            }).start();
        } else if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            FancyHolograms.getInstance().getFancyHologramsConfig().reload();
            FancyHolograms.getInstance().getHologramManager().reloadHolograms();
            MessageHelper.success(sender, "Reloaded config and holograms");
        } else if(args.length == 1 && args[0].equalsIgnoreCase("save")){
            FancyHolograms.getInstance().getHologramManager().saveHolograms(true);
            MessageHelper.success(sender, "Saved all holograms");
        }

        return false;
    }
}
