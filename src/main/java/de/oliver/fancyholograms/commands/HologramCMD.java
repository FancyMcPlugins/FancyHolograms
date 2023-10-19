package de.oliver.fancyholograms.commands;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.hologram.*;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class HologramCMD implements CommandExecutor, TabCompleter {

    @NotNull
    private final FancyHolograms plugin;

    public HologramCMD(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }

    public static boolean callModificationEvent(@NotNull final Hologram hologram, @NotNull final Player player, @NotNull final HologramData updatedData, @NotNull final HologramUpdateEvent.HologramModification modification) {
        final var result = new HologramUpdateEvent(hologram, player, updatedData, modification).callEvent();

        if (!result) {
            MessageHelper.error(player, "Cancelled hologram modification");
        }

        return result;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            MessageHelper.info(player, Constants.HELP_TEXT + (!FancyHolograms.isUsingFancyNpcs() ? "" : "\n" + Constants.HELP_TEXT_NPCS));
            return true;
        }


        if (args[0].equalsIgnoreCase("list")) {
            return new ListCMD().run(player, null, args);
        }


        if (args.length < 2) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }


        if (args[0].equalsIgnoreCase("create")) {
            return new CreateCMD().run(player, null, args);
        }


        final var hologram = this.plugin.getHologramsManager().getHologram(args[1]).orElse(null);
        if (hologram == null) {
            MessageHelper.error(player, "Could not find hologram: '" + args[1] + "'");
            return false;
        }


        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "remove" -> new RemoveCMD().run(player, hologram, args);
            case "teleport" -> new TeleportCMD().run(player, hologram, args);
            case "copy" -> new CopyCMD().run(player, hologram, args);
            case "edit" -> {
                if (args.length < 3) {
                    MessageHelper.error(player, "Wrong usage: /hologram help");
                    yield false;
                }

                final var updated = edit(player, hologram, args);

                if (updated) {
                    hologram.updateHologram();

                    this.plugin.getHologramsManager().refreshHologramForPlayersInWorld(hologram);
                }

                yield updated;
            }
            default -> false;
        };
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        // /holo {tab:action}
        if (args.length == 1) {
            return Stream.of("help", "list", "teleport", "create", "remove", "edit", "copy")
                    .filter(input -> input.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        // /holo [action] {tab:hologram}
        if (args.length == 2) {
            final var action = args[0].toLowerCase(Locale.ROOT);

            if (!Set.of("teleport", "remove", "edit", "copy").contains(action)) {
                return Collections.emptyList();
            }

            return this.plugin.getHologramsManager()
                    .getHolograms()
                    .stream()
                    .map(hologram -> hologram.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        final var hologram = this.plugin.getHologramsManager().getHologram(args[1]).orElse(null);
        if (hologram == null) {
            return Collections.emptyList();
        }

        // /holo edit [hologram] {tab:option}
        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("edit")) {
                return Collections.emptyList();
            }

            final var usingNpcs = FancyHolograms.isUsingFancyNpcs();

            return Stream.of("position", "moveHere", "moveTo", "rotate", "rotatepitch", "setLine", "addLine", "removeLine", "insertAfter", "insertBefore", "billboard", "scale", "background", "updateTextInterval", "visibilityDistance", "shadowRadius", "shadowStrength", "textShadow", "textAlignment", usingNpcs ? "linkWithNpc" : "", usingNpcs ? "unlinkWithNpc" : "")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        if (!args[0].equalsIgnoreCase("edit")) {
            return Collections.emptyList();
        }

        // /holo edit [hologram] [option] {tab:contextual}
        if (args.length == 4) {
            final var suggestions = switch (args[2].toLowerCase(Locale.ROOT)) {
                case "billboard" -> {
                    final var values = new ArrayList<>(List.of(Display.Billboard.values()));

                    values.remove(hologram.getData().getBillboard());

                    yield values.stream().map(Enum::name);
                }
                case "background" -> {
                    final var colors = new ArrayList<>(NamedTextColor.NAMES.keys());

                    colors.add("reset");
                    colors.add("default");
                    colors.add("transparent");

                    final var current = hologram.getData().getBackground();

                    if (current == null) {
                        colors.remove("reset");
                        colors.remove("default");
                    } else if (current == Hologram.TRANSPARENT) {
                        colors.remove("transparent");
                    } else if (current instanceof NamedTextColor named) {
                        colors.remove(named.toString());
                    } else {
                        colors.add(current.asHexString()); // suggest the current hex value for each of use...
                    }

                    yield colors.stream();
                }
                case "textshadow" -> Stream.of(!hologram.getData().isTextHasShadow()).map(Object::toString);
                case "textalignment" -> Arrays.stream(TextDisplay.TextAlignment.values()).map(Enum::name);
                case "setline", "removeline" ->
                        IntStream.range(1, hologram.getData().getText().size() + 1).mapToObj(Integer::toString);
                case "linkwithnpc" -> {
                    if (!FancyHolograms.isUsingFancyNpcs()) {
                        yield Stream.<String>empty();
                    }

                    yield FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream().map(npc -> npc.getData().getName());
                }
                default -> null;
            };

            if (suggestions != null) {
                return suggestions.filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase(Locale.ROOT)))
                        .toList();
            }
        }

        // /holo edit [hologram] setline [number] {tab:line_text}
        if (args[2].equalsIgnoreCase("setline")) {
            final var index = Ints.tryParse(args[3]);
            if (index == null || index < 1 || index > hologram.getData().getText().size()) {
                return Collections.emptyList();
            }

            return List.of(hologram.getData().getText().get(index - 1));
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

        return Collections.emptyList();
    }

    private boolean edit(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final String[] args) {
        final var action = args[2].toLowerCase();

        if (action.equals("position") || action.equals("movehere")) {
            return new MoveHereCMD().run(player, hologram, args);
        } else if (action.equals("unlinkwithnpc")) {
            return new UnlinkWithNpcCMD().run(player, hologram, args);
        }

        if (args.length == 3) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        return switch (action) {
            case "addline" -> new AddLineCMD().run(player, hologram, args);
            case "setline" -> new SetLineCMD().run(player, hologram, args);
            case "removeline" -> new RemoveLineCMD().run(player, hologram, args);
            case "insertbefore" -> new InsertBeforeCMD().run(player, hologram, args);
            case "insertafter" -> new InsertAfterCMD().run(player, hologram, args);
            case "moveto" -> new MoveToCMD().run(player, hologram, args);
            case "rotate" -> new RotateCMD().run(player, hologram, args);
            case "rotatepitch" -> new RotatePitchCMD().run(player, hologram, args);
            case "billboard" -> new BillboardCMD().run(player, hologram, args);
            case "scale" -> new ScaleCMD().run(player, hologram, args);
            case "background" -> new BackgroundCMD().run(player, hologram, args);
            case "textshadow" -> new TextShadowCMD().run(player, hologram, args);
            case "textalignment" -> new TextAlignmentCMD().run(player, hologram, args);
            case "shadowradius" -> new ShadowRadiusCMD().run(player, hologram, args);
            case "shadowstrength" -> new ShadowStrengthCMD().run(player, hologram, args);
            case "updatetextinterval" -> new UpdateTextIntervalCMD().run(player, hologram, args);
            case "visibilitydistance" -> new VisibilityDistanceCMD().run(player, hologram, args);
            case "linkwithnpc" -> new LinkWithNpcCMD().run(player, hologram, args);
            default -> false;
        };
    }

}
