package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancylib.MessageHelper;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FancyHologramsTestCMD extends Command {

    @NotNull
    private final FancyHolograms plugin;

    public FancyHologramsTestCMD(@NotNull final FancyHolograms plugin) {
        super("FancyHologramsTest");
        setPermission("fancyholograms.admin");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Arrays.asList("spawn100", "test1");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (!(sender instanceof Player p)) {
            MessageHelper.error(sender, "Only players can use this command!");
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("spawn100")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int n = (i * 10 + j) + 1;
                    TextHologramData textData = TextHologramData.getDefault("holo-" + n);
                    textData.setText(Arrays.asList(
                            "<rainbow><b>This is a test hologram! (#" + n + ")</b></rainbow>",
                            "<red>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                            "<green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                            "<yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                            "<gradient:red:green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                            "<gradient:green:yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris."
                    ));
                    textData.setTextUpdateInterval(100);

                    DisplayHologramData displayData = DisplayHologramData.getDefault(p.getLocation().clone().add(5 * i + 1, 0, 5 * j + 1));
                    displayData.setScale(new Vector3f(.5f, .5f, .5f));
                    displayData.setVisibilityDistance(100);

                    HologramData data = new HologramData("holo-" + n, displayData, HologramType.TEXT, textData);
                    Hologram hologram = this.plugin.getHologramsManager().create(data);
                    hologram.createHologram();
                    hologram.checkAndUpdateShownStateForPlayer(p);
                }
            }

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("test1")) {
            TextHologramData textData = TextHologramData.getDefault("holo-test1");
            textData.setText(Arrays.asList(
                    "<rainbow><b>This is a test hologram!</b></rainbow>",
                    "<red>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                    "<green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                    "<yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                    "<gradient:red:green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
                    "<gradient:green:yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris."
            ));
            textData.setTextUpdateInterval(100);
            textData.setTextAlignment(TextDisplay.TextAlignment.CENTER);
            textData.setBackground(TextColor.color(78, 237, 176));
            textData.setTextShadow(true);

            DisplayHologramData displayData = DisplayHologramData.getDefault(p.getLocation());
            displayData.setScale(new Vector3f(2, 2, 2));
            displayData.setBillboard(Display.Billboard.CENTER);
            displayData.setBrightness(new Display.Brightness(15, 15));
            displayData.setShadowRadius(3);
            displayData.setShadowStrength(3);
            displayData.setVisibilityDistance(100);

            HologramData data = new HologramData("holo-test1", displayData, HologramType.TEXT, textData);
            Hologram hologram = this.plugin.getHologramsManager().create(data);
            hologram.createHologram();
            hologram.checkAndUpdateShownStateForPlayer(p);
        }

        return false;
    }
}
