package de.oliver.fancyholograms.commands;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.commands.hologram.*;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancyholograms.util.PluginUtils;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class HologramCMD extends Command {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public HologramCMD(@NotNull final FancyHologramsPlugin plugin) {
        super("hologram", "Main command for the FancyHolograms plugin", "/hologram help", List.of("holograms", "holo", "fholo"));

        setPermission("fancyholograms.admin");

        this.plugin = plugin;
    }

    public static boolean callModificationEvent(@NotNull final Hologram hologram, @NotNull final CommandSender player, @NotNull final HologramData updatedData, @NotNull final HologramUpdateEvent.HologramModification modification) {
        final var result = new HologramUpdateEvent(hologram, player, updatedData, modification).callEvent();

        if (!result) {
            MessageHelper.error(player, "Cancelled hologram modification");
        }

        return result;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            MessageHelper.info(sender, Constants.HELP_TEXT + (!PluginUtils.isFancyNpcsEnabled() ? "" : "\n" + Constants.HELP_TEXT_NPCS));
            return false;
        }


        if (args[0].equalsIgnoreCase("list")) {
            return new ListCMD().run(sender, null, args);
        }


        if (args.length < 2) {
            MessageHelper.error(sender, "Wrong usage: /hologram help");
            return false;
        }


        if (args[0].equalsIgnoreCase("create")) {
            return new CreateCMD().run(sender, null, args);
        }

        if (args[0].equalsIgnoreCase("nearby")) {
            return new NearbyCMD().run(sender, null, args);
        }

        final var hologram = this.plugin.getRegistry().get(args[1]).orElse(null);
        if (hologram == null) {
            MessageHelper.error(sender, "Could not find hologram: '" + args[1] + "'");
            return false;
        }


        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "info" -> new InfoCMD().run(sender, hologram, args);
            case "remove" -> new RemoveCMD().run(sender, hologram, args);
            case "teleport" -> new TeleportCMD().run(sender, hologram, args);
            case "copy" -> new CopyCMD().run(sender, hologram, args);
            case "edit" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /hologram help");
                    yield false;
                }

                final var updated = edit(sender, hologram, args);

                if (updated) {
                    if (sender instanceof Player p) {
                        plugin.getController().refreshHologram(hologram, p);
                    }

                    //TODO: idk
                    // hologram.queueUpdate();
                }

                yield updated;
            }
            default -> false;
        };
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        // /holo {tab:action}
        if (args.length == 1) {
            return Stream.of("help", "list", "teleport", "create", "remove", "edit", "copy", "info", "nearby").filter(input -> input.startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
        }

        // /holo create {tab:type}
        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("text", "item", "block");
        }

        // /holo [action] {tab:hologram}
        if (args.length == 2) {
            final var action = args[0].toLowerCase(Locale.ROOT);

            if (!Set.of("teleport", "remove", "edit", "copy", "info").contains(action)) {
                return Collections.emptyList();
            }

            return this.plugin.getRegistry().getAllPersistent()
                    .stream()
                    .map(hologram -> hologram.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        final var hologram = this.plugin.getRegistry().get(args[1]).orElse(null);
        if (hologram == null) {
            return Collections.emptyList();
        }

        HologramType type = hologram.getData().getType();

        // /holo edit [hologram] {tab:option}
        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("edit")) {
                return Collections.emptyList();
            }

            final var usingNpcs = PluginUtils.isFancyNpcsEnabled();

            List<String> suggestions = new ArrayList<>(Arrays.asList("position", "moveHere", "center", "moveTo", "rotate", "rotatepitch", "billboard", "scale", "translate", "visibilityDistance", "visibility", "shadowRadius", "shadowStrength", "brightness", usingNpcs ? "linkWithNpc" : "", usingNpcs ? "unlinkWithNpc" : ""));
            suggestions.addAll(type.getCommands());

            return suggestions.stream().filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase(Locale.ROOT))).toList();
        }

        if (!args[0].equalsIgnoreCase("edit")) {
            return Collections.emptyList();
        }

        // /holo edit [hologram] [option] {tab:contextual}
        if (args.length == 4) {
            final var suggestions = switch (args[2].toLowerCase(Locale.ROOT)) {
                case "billboard" -> {
                    final var values = new ArrayList<>(List.of(Display.Billboard.values()));

                    if (hologram.getData() instanceof DisplayHologramData displayData) {
                        values.remove(displayData.getBillboard());
                    }

                    yield values.stream().map(Enum::name);
                }
                case "background" -> {
                    TextHologramData textData = (TextHologramData) hologram.getData();
                    final var colors = new ArrayList<>(NamedTextColor.NAMES.keys());

                    colors.add("reset");
                    colors.add("default");
                    colors.add("transparent");

                    final var current = textData.getBackground();

                    if (current == null) {
                        colors.remove("reset");
                        colors.remove("default");
                    } else if (current == Hologram.TRANSPARENT) {
                        colors.remove("transparent");
                    } else {
                        NamedTextColor named = current.getAlpha() == 255 ? NamedTextColor.namedColor(current.asRGB()) : null;
                        colors.add(named != null ? named.toString() : '#' + Integer.toHexString(current.asARGB()));
                    }

                    yield colors.stream();
                }
                case "textshadow" -> {
                    TextHologramData textData = (TextHologramData) hologram.getData();
                    yield Stream.of(!textData.hasTextShadow()).map(Object::toString);
                }
                case "brightness" -> Stream.of("block", "sky");
                case "textalignment" -> Arrays.stream(TextDisplay.TextAlignment.values()).map(Enum::name);
                case "setline", "removeline" -> {
                    TextHologramData textData = (TextHologramData) hologram.getData();
                    yield IntStream.range(1, textData.getText().size() + 1).mapToObj(Integer::toString);
                }
                case "linkwithnpc" -> {
                    if (!PluginUtils.isFancyNpcsEnabled()) {
                        yield Stream.<String>empty();
                    }

                    yield FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream().map(npc -> npc.getData().getName());
                }
                case "block" -> Arrays.stream(Material.values()).filter(Material::isBlock).map(Enum::name);
                case "seethrough" -> Stream.of("true", "false");
                case "visibility" -> new VisibilityCMD().tabcompletion(sender, hologram, args).stream();

                default -> null;
            };

            if (suggestions != null) {
                return suggestions.filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase(Locale.ROOT))).toList();
            }
        }

        // /holo edit [hologram] setline [number] {tab:line_text}
        if (args[2].equalsIgnoreCase("setline")) {
            TextHologramData textData = (TextHologramData) hologram.getData();

            final var index = Ints.tryParse(args[3]);
            if (index == null || index < 1 || index > textData.getText().size()) {
                return Collections.emptyList();
            }

            return List.of(textData.getText().get(index - 1));
        }

        // /holo edit [hologram] moveto {tab:x} {tab:y} {tab:z}
        if (args[2].equalsIgnoreCase("moveto")) {
            if (!(sender instanceof Player player)) {
                return Collections.emptyList();
            }

            final var suggestions = new ArrayList<String>();
            suggestions.add("~");
            suggestions.add("~~");

            if (args.length == 7) {
                suggestions.add(String.valueOf(player.getLocation().getYaw()));
            }

            if (args.length == 8) {
                suggestions.add(String.valueOf(player.getLocation().getPitch()));
            }

            final var target = player.getTargetBlockExact(10);
            if (target != null) {
                final var coordinate = switch (args.length) {
                    case 4 -> target.getX();
                    case 5 -> target.getY();
                    case 6 -> target.getZ();
                    default -> null;
                };

                suggestions.add(String.valueOf(coordinate));
            }

            return suggestions;
        }

        if(args[2].equalsIgnoreCase("brightness")) {
            if(args.length == 4) {
                return List.of("block", "sky");
            }

            if(args.length > 5) {
                return Collections.emptyList();
            }

            return List.of("0", "5", "10", "15");
        }

        return Collections.emptyList();
    }

    private boolean edit(@NotNull final CommandSender player, @NotNull final Hologram hologram, @NotNull final String[] args) {
        final var action = args[2].toLowerCase();

        // actions without a data
        switch (action) {
            case "position", "movehere" -> {
                return new MoveHereCMD().run(player, hologram, args);
            }
            case "center" -> {
                return new CenterCMD().run(player, hologram, args);
            }
            case "unlinkwithnpc" -> {
                return new UnlinkWithNpcCMD().run(player, hologram, args);
            }
            case "item" -> {
                return new ItemCMD().run(player, hologram, args);
            }
        }

        if (args.length == 3) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        return switch (action) {
            // display data
            case "moveto" -> new MoveToCMD().run(player, hologram, args);
            case "rotate" -> new RotateCMD().run(player, hologram, args);
            case "rotatepitch" -> new RotatePitchCMD().run(player, hologram, args);
            case "billboard" -> new BillboardCMD().run(player, hologram, args);
            case "scale" -> new ScaleCMD().run(player, hologram, args);
            case "translate" -> new TranslateCommand().run(player, hologram, args);
            case "updatetextinterval" -> new UpdateTextIntervalCMD().run(player, hologram, args);
            case "visibilitydistance" -> new VisibilityDistanceCMD().run(player, hologram, args);
            case "visibility" -> new VisibilityCMD().run(player, hologram, args);
            case "linkwithnpc" -> new LinkWithNpcCMD().run(player, hologram, args);
            case "shadowradius" -> new ShadowRadiusCMD().run(player, hologram, args);
            case "shadowstrength" -> new ShadowStrengthCMD().run(player, hologram, args);
            case "brightness" -> new BrightnessCMD().run(player, hologram, args);

            // text data
            case "background" -> new BackgroundCMD().run(player, hologram, args);
            case "addline" -> new AddLineCMD().run(player, hologram, args);
            case "setline" -> new SetLineCMD().run(player, hologram, args);
            case "removeline" -> new RemoveLineCMD().run(player, hologram, args);
            case "insertbefore" -> new InsertBeforeCMD().run(player, hologram, args);
            case "insertafter" -> new InsertAfterCMD().run(player, hologram, args);
            case "textshadow" -> new TextShadowCMD().run(player, hologram, args);
            case "textalignment" -> new TextAlignmentCMD().run(player, hologram, args);
            case "seethrough" -> new SeeThroughCMD().run(player, hologram, args);

            // block data
            case "block" -> new BlockCMD().run(player, hologram, args);

            default -> false;
        };
    }

}
