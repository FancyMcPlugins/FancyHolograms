package de.oliver.fancyholograms.commands;

import com.google.common.base.Enums;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancyholograms.api.events.HologramCreateEvent;
import de.oliver.fancyholograms.api.events.HologramDeleteEvent;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.Npc;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class HologramCMD implements CommandExecutor, TabCompleter {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public HologramCMD(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            MessageHelper.info(player, Constants.HELP_TEXT + (!FancyHologramsPlugin.isUsingFancyNpcs() ? "" : "\n" + Constants.HELP_TEXT_NPCS));
            return true;
        }


        if (args[0].equalsIgnoreCase("list")) {
            final var holograms = this.plugin.getHologramsManager().getHolograms();

            if (holograms.isEmpty()) {
                MessageHelper.warning(player, "There are no holograms. Use '/hologram create' to create one");
            } else {
                MessageHelper.info(player, "<b>List of all holograms:</b>");

                for (final var hologram : holograms) {
                    final var location = hologram.getData().getLocation();
                    if (location == null || location.getWorld() == null) {
                        continue;
                    }

                    MessageHelper.info(sender,
                            "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'%s'> - %s (%s/%s/%s)</click></hover>"
                                    .formatted("/hologram teleport " + hologram.getData().getName(),
                                            hologram.getData().getName(),
                                            Constants.DECIMAL_FORMAT.format(location.x()),
                                            Constants.DECIMAL_FORMAT.format(location.y()),
                                            Constants.DECIMAL_FORMAT.format(location.z())));
                }
            }

            return true;
        }


        if (args.length < 2) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }


        if (args[0].equalsIgnoreCase("create")) {
            return create(player, args[1]);
        }


        final var hologram = this.plugin.getHologramsManager().getHologram(args[1]).orElse(null);
        if (hologram == null) {
            MessageHelper.error(player, "Could not find hologram: '" + args[1] + "'");
            return false;
        }


        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "remove" -> remove(player, hologram);
            case "teleport" -> teleport(player, hologram);
            case "copy" -> {
                if (args.length < 3) {
                    MessageHelper.error(player, "Wrong usage: /hologram help");
                    yield false;
                }

                yield copy(player, hologram, args[2]);
            }
            case "edit" -> {
                if (args.length < 3) {
                    MessageHelper.error(player, "Wrong usage: /hologram help");
                    yield false;
                }

                final var updated = edit(player, hologram, new ArrayList<>(List.of(args).subList(2, args.length)));

                if (updated) {
                    hologram.updateHologram();

                    this.plugin.getHologramsManager()
                            .refreshHologramForPlayersInWorld(hologram);
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
                    .filter(input -> input.startsWith(args[1].toLowerCase(Locale.ROOT)))
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

            final var usingNpcs = FancyHologramsPlugin.isUsingFancyNpcs();

            return Stream.of("position", "moveTo", "rotate", "setLine", "addLine", "removeLine", "insertAfter", "insertBefore", "billboard", "scale", "background", "updateTextInterval", "shadowRadius", "shadowStrength", "textShadow", usingNpcs ? "linkWithNpc" : "", usingNpcs ? "unlinkWithNpc" : "")
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
                case "setline", "removeline" ->
                        IntStream.range(1, hologram.getData().getText().size() + 1).mapToObj(Integer::toString);
                case "linkwithnpc" -> {
                    if (!FancyHologramsPlugin.isUsingFancyNpcs()) {
                        yield Stream.<String>empty();
                    }

                    yield FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream().map(Npc::getName);
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


    private boolean create(@NotNull final Player player, @NotNull final String name) {
        if (this.plugin.getHologramsManager().getHologram(name).isPresent()) {
            MessageHelper.error(player, "There already exists a hologram with this name");
            return false;
        }

        final var data = new HologramData(name);
        data.setText(List.of("Edit this line with /hologram edit " + name));
        data.setLocation(player.getLocation().clone());

        final var hologram = this.plugin.getHologramsManager().create(data);

        if (!new HologramCreateEvent(hologram, player).callEvent()) {
            MessageHelper.error(player, "Creating the hologram was cancelled");
            return false;
        }

        hologram.createHologram();
        hologram.showHologram(Bukkit.getOnlinePlayers());

        this.plugin.getHologramsManager().addHologram(hologram);

        MessageHelper.success(player, "Created the hologram");
        return true;
    }

    private boolean remove(@NotNull final Player player, @NotNull final Hologram hologram) {
        if (!new HologramDeleteEvent(hologram, player).callEvent()) {
            MessageHelper.error(player, "Removing the hologram was cancelled");
            return false;
        }

        hologram.hideHologram(Bukkit.getOnlinePlayers());
        hologram.deleteHologram();

        this.plugin.getHologramsManager().removeHologram(hologram);

        MessageHelper.success(player, "Removed the hologram");
        return true;
    }

    private boolean copy(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final String name) {
        if (this.plugin.getHologramsManager().getHologram(name).isPresent()) {
            MessageHelper.error(player, "There already exists a hologram with this name");
            return false;
        }

        final var data = new HologramData(name, hologram.getData());
        data.setLocation(player.getLocation());

        final var copy = this.plugin.getHologramsManager().create(data);

        if (!new HologramCreateEvent(copy, player).callEvent()) {
            MessageHelper.error(player, "Creating the copied hologram was cancelled");
            return false;
        }

        copy.createHologram();
        copy.showHologram(Bukkit.getOnlinePlayers());

        this.plugin.getHologramsManager().addHologram(copy);

        MessageHelper.success(player, "Copied the hologram");
        return true;
    }

    private boolean teleport(@NotNull final Player player, @NotNull final Hologram hologram) {
        final var location = hologram.getData().getLocation();

        if (location == null || location.getWorld() == null || !player.teleport(location)) {
            MessageHelper.error(player, "Could not teleport to the hologram");
            return false;
        }

        MessageHelper.success(player, "Teleported you to the hologram");
        return true;
    }

    private boolean edit(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final List<String> args) {
        final var action = args.remove(0).toLowerCase(Locale.ROOT);

        if (action.equals("position")) {
            return editLocation(player, hologram, player.getLocation());
        }

        if (args.isEmpty()) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        return switch (action) {
            case "addline" -> editSetLine(player, hologram, Integer.MAX_VALUE, String.join(" ", args));
            case "setline" -> {
                final var index = Ints.tryParse(args.remove(0));
                if (index == null) {
                    MessageHelper.error(player, "Could not parse line number");
                    yield false;
                }

                yield editSetLine(player, hologram, index - 1, String.join(" ", args));
            }
            case "removeline" -> {
                final var index = Ints.tryParse(args.remove(0));
                if (index == null) {
                    MessageHelper.error(player, "Could not parse line number");
                    yield false;
                }

                yield editSetLine(player, hologram, index - 1, null);
            }
            case "insertbefore" -> {
                final var index = Ints.tryParse(args.remove(0));
                if (index == null) {
                    MessageHelper.error(player, "Could not parse line number");
                    yield false;
                }

                yield editAddLine(player, hologram, index - 1, String.join(" ", args));
            }
            case "insertafter" -> {
                final var index = Ints.tryParse(args.remove(0));
                if (index == null) {
                    MessageHelper.error(player, "Could not parse line number");
                    yield false;
                }

                yield editAddLine(player, hologram, index, String.join(" ", args));
            }
            case "moveto" -> {
                if (args.size() < 3) {
                    MessageHelper.error(player, "Wrong usage: /hologram help");
                    yield false;
                }

                final var x = calculateCoordinate(args.remove(0), hologram.getData().getLocation(), player.getLocation(), Location::x);
                final var y = calculateCoordinate(args.remove(0), hologram.getData().getLocation(), player.getLocation(), Location::y);
                final var z = calculateCoordinate(args.remove(0), hologram.getData().getLocation(), player.getLocation(), Location::z);

                if (x == null || y == null || z == null) {
                    MessageHelper.error(player, "Could not parse position");
                    yield false;
                }

                final var location = new Location(player.getWorld(), x, y, z);

                if (!args.isEmpty()) {
                    final var yaw = calculateCoordinate(args.remove(0), hologram.getData().getLocation(), player.getLocation(), Location::getYaw);

                    if (yaw == null) {
                        MessageHelper.error(player, "Could not parse yaw");
                        yield false;
                    }

                    location.setYaw(yaw.floatValue());
                }

                yield editLocation(player, hologram, location);
            }
            case "rotate" -> {
                final var yaw = calculateCoordinate(args.remove(0), hologram.getData().getLocation(), player.getLocation(), loc -> loc.getYaw() + 180f);
                Location location = hologram.getData().getLocation().clone();
                location.setYaw(yaw.floatValue() - 180f);

                yield editLocation(player, hologram, location);
            }
            case "billboard" -> {
                final var billboard = Enums.getIfPresent(Display.Billboard.class, args.remove(0).toUpperCase(Locale.ROOT)).orNull();

                if (billboard == null) {
                    MessageHelper.error(player, "Could not parse billboard");
                    yield false;
                }

                yield editBillboard(player, hologram, billboard);
            }
            case "scale" -> {
                final var scale = Floats.tryParse(args.remove(0));

                if (scale == null) {
                    MessageHelper.error(player, "Could not parse scale");
                    yield false;
                }

                yield editScale(player, hologram, scale);
            }
            case "background" -> {
                final var color = args.remove(0).toLowerCase(Locale.ROOT);

                final TextColor background;

                if (color.equals("reset") || color.equals("default")) {
                    background = null;
                } else {
                    if (color.equals("transparent")) {
                        background = Hologram.TRANSPARENT;
                    } else if (color.startsWith("#")) {
                        background = TextColor.fromHexString(color);
                    } else {
                        background = NamedTextColor.NAMES.value(color.replace(' ', '_'));
                    }

                    if (background == null) {
                        MessageHelper.error(player, "Could not parse background color");
                        yield false;
                    }
                }

                yield editBackground(player, hologram, background);
            }
            case "textshadow" -> {
                final var enabled = switch (args.remove(0).toLowerCase(Locale.ROOT)) {
                    case "true" -> true;
                    case "false" -> false;
                    default -> null;
                };

                if (enabled == null) {
                    MessageHelper.error(player, "Could not parse text shadow flag");
                    yield false;
                }

                yield editHasTextShadow(player, hologram, enabled);
            }
            case "shadowradius" -> {
                final var radius = Floats.tryParse(args.remove(0));

                if (radius == null) {
                    MessageHelper.error(player, "Could not parse shadow radius");
                    yield false;
                }

                yield editShadowRadius(player, hologram, radius);
            }
            case "shadowstrength" -> {
                final var strength = Floats.tryParse(args.remove(0));

                if (strength == null) {
                    MessageHelper.error(player, "Could not parse shadow strength");
                    yield false;
                }

                yield editShadowStrength(player, hologram, strength);
            }
            case "updatetextinterval" -> {
                final var text = args.remove(0).toLowerCase(Locale.ROOT);

                final Integer interval;

                if (text.equals("never") || text.equals("off") || text.equals("none")) {
                    interval = -1;
                } else {

                    var multiplier = 1;

                    if (!text.isEmpty()) {
                        switch (text.charAt(text.length() - 1)) {
                            case 's' -> multiplier = 20;
                            case 'm' -> multiplier = 20 * 60;
                        }
                    }

                    final var time = Ints.tryParse(multiplier == 1 ? text : text.substring(0, text.length() - 1));

                    if (time == null) {
                        interval = null;
                    } else {
                        interval = time * multiplier;
                    }
                }

                if (interval == null) {
                    MessageHelper.error(player, "Could not parse text update interval");
                    yield false;
                }

                yield editTextUpdateInterval(player, hologram, Math.max(-1, interval));
            }
            case "linkwithnpc" -> {
                if (!FancyHologramsPlugin.isUsingFancyNpcs()) {
                    MessageHelper.warning(player, "You need to install the FancyNpcs plugin for this functionality to work");
                    MessageHelper.warning(player, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
                    yield false;
                }

                yield editLinkWithNpc(player, hologram, args.remove(0));
            }
            case "unlinkwithnpc" -> {
                if (!FancyHologramsPlugin.isUsingFancyNpcs()) {
                    MessageHelper.warning(player, "You need to install the FancyNpcs plugin for this functionality to work");
                    MessageHelper.warning(player, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
                    yield false;
                }

                yield editUnlinkWithNpc(player, hologram);
            }
            default -> false;
        };
    }


    private boolean editSetLine(@NotNull final Player player, @NotNull final Hologram hologram, final int index, @Nullable final String text) {
        if (index < 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        final var lines = new ArrayList<>(hologram.getData().getText());

        if (index >= lines.size()) {
            lines.add(text == null ? " " : text);
        } else if (text == null) {
            lines.remove(index);
        } else {
            lines.set(index, text);
        }

        final var copied = hologram.getData().copy();
        copied.setText(lines);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        hologram.getData().setText(copied.getText());

        MessageHelper.success(player, "Changed text for line " + (Math.min(index, lines.size() - 1) + 1));
        return true;
    }

    private boolean editAddLine(@NotNull final Player player, @NotNull final Hologram hologram, final int index, @NotNull final String text) {
        if (index < 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        final var lines = new ArrayList<>(hologram.getData().getText());
        lines.add(Math.min(index, lines.size()), text);

        final var copied = hologram.getData().copy();
        copied.setText(lines);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        hologram.getData().setText(copied.getText());

        MessageHelper.success(player, "Inserted line");
        return true;
    }

    private boolean editLocation(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final Location location) {
        if (hologram.getData().getLinkedNpcName() != null) {
            MessageHelper.error(player, "This hologram is linked with an NPC");
            MessageHelper.error(player, "To unlink: /hologram edit " + hologram.getData().getName() + " unlinkWithNpc");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setLocation(location);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return false;
        }

        final var updatedLocation = copied.getLocation() == null ? location : copied.getLocation(); // note: maybe should fall back to original location?
        hologram.getData().setLocation(updatedLocation);

        MessageHelper.success(player, "Moved the hologram to %s/%s/%s %s\u00B0".formatted(
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.x()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.y()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.z()),
                Constants.COORDINATES_DECIMAL_FORMAT.format((updatedLocation.getYaw() + 180f) % 360f)
        ));

        return true;
    }

    private boolean editBillboard(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final Display.Billboard billboard) {
        if (billboard == hologram.getData().getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setBillboard(billboard);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (copied.getBillboard() == hologram.getData().getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        hologram.getData().setBillboard(copied.getBillboard());

        MessageHelper.success(player, "Changed the billboard to " + StringUtils.capitalize(billboard.name().toLowerCase(Locale.ROOT)));
        return true;
    }

    private boolean editScale(@NotNull final Player player, @NotNull final Hologram hologram, final float scale) {
        if (Float.compare(scale, hologram.getData().getScale()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setScale(scale);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SCALE)) {
            return false;
        }

        if (Float.compare(copied.getScale(), hologram.getData().getScale()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        hologram.getData().setScale(copied.getScale());

        MessageHelper.success(player, "Changed scale to " + scale);
        return true;
    }

    private boolean editBackground(@NotNull final Player player, @NotNull final Hologram hologram, @Nullable final TextColor background) {
        if (Objects.equals(background, hologram.getData().getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setBackground(background);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BACKGROUND)) {
            return false;
        }

        if (Objects.equals(copied.getBackground(), hologram.getData().getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        hologram.getData().setBackground(copied.getBackground());

        MessageHelper.success(player, "Changed background color");
        return true;
    }

    private boolean editHasTextShadow(@NotNull final Player player, @NotNull final Hologram hologram, final boolean enabled) {
        if (enabled == hologram.getData().isTextHasShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setTextHasShadow(enabled);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_SHADOW)) {
            return false;
        }

        if (enabled == hologram.getData().isTextHasShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        hologram.getData().setTextHasShadow(copied.isTextHasShadow());

        MessageHelper.success(player, "Changed text shadow");
        return true;
    }

    private boolean editShadowRadius(@NotNull final Player player, @NotNull final Hologram hologram, final float radius) {
        if (Float.compare(radius, hologram.getData().getShadowRadius()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow radius");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setShadowRadius(radius);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SHADOW_RADIUS)) {
            return false;
        }

        if (Float.compare(copied.getShadowRadius(), hologram.getData().getShadowRadius()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow radius");
            return false;
        }

        hologram.getData().setShadowRadius(copied.getShadowRadius());

        MessageHelper.success(player, "Changed shadow radius");
        return true;
    }

    private boolean editShadowStrength(@NotNull final Player player, @NotNull final Hologram hologram, final float strength) {
        if (Float.compare(strength, hologram.getData().getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setShadowStrength(strength);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SHADOW_STRENGTH)) {
            return false;
        }

        if (Float.compare(copied.getShadowStrength(), hologram.getData().getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        hologram.getData().setShadowStrength(copied.getShadowStrength());

        MessageHelper.success(player, "Changed shadow strength");
        return true;
    }

    private boolean editTextUpdateInterval(@NotNull final Player player, @NotNull final Hologram hologram, final int interval) {
        if (interval == hologram.getData().getTextUpdateInterval()) {
            MessageHelper.warning(player, "This hologram already has this text update interval");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setTextUpdateInterval(interval);

        if (!callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.UPDATE_TEXT_INTERVAL)) {
            return false;
        }

        if (copied.getTextUpdateInterval() == hologram.getData().getTextUpdateInterval()) {
            MessageHelper.warning(player, "This hologram already has this text update interval");
            return false;
        }

        hologram.getData().setTextUpdateInterval(copied.getTextUpdateInterval());

        MessageHelper.success(player, "Changed the text update interval");
        return true;
    }

    private boolean editLinkWithNpc(@NotNull final Player player, @NotNull final Hologram hologram, @NotNull final String name) {
        if (hologram.getData().getLinkedNpcName() != null) {
            MessageHelper.error(player, "This hologram is already linked with an NPC");
            return false;
        }

        final var npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
        if (npc == null) {
            MessageHelper.error(player, "Could not find NPC with that name");
            return false;
        }

        hologram.getData().setLinkedNpcName(npc.getName());

        this.plugin.getHologramsManager().syncHologramWithNpc(hologram);

        MessageHelper.success(player, "Linked hologram with NPC");
        return true;
    }

    private boolean editUnlinkWithNpc(@NotNull final Player player, @NotNull final Hologram hologram) {
        if (hologram.getData().getLinkedNpcName() == null) {
            MessageHelper.error(player, "This hologram is not linked with an NPC");
            return false;
        }

        final var npc = FancyNpcs.getInstance().getNpcManager().getNpc(hologram.getData().getLinkedNpcName());

        hologram.getData().setLinkedNpcName(null);

        if (npc != null) {
            npc.updateDisplayName(npc.getName());
        }

        MessageHelper.success(player, "Unlinked hologram with NPC");
        return true;
    }


    private boolean callModificationEvent(@NotNull final Hologram hologram, @NotNull final Player player, @NotNull final HologramData updatedData, @NotNull final HologramUpdateEvent.HologramModification modification) {
        final var result = new HologramUpdateEvent(hologram, player, updatedData, modification).callEvent();

        if (!result) {
            MessageHelper.error(player, "Cancelled hologram modification");
        }

        return result;
    }


    private @Nullable Double calculateCoordinate(@NotNull final String text, @Nullable final Location originLocation, @NotNull final Location callerLocation, @NotNull final Function<Location, Number> extractor) {
        final var number = Doubles.tryParse(StringUtils.stripStart(text, "~"));
        final var target = text.startsWith("~~") ? callerLocation : text.startsWith("~") ? originLocation : null;

        if (number == null) {
            return target == null ? null : extractor.apply(target).doubleValue();
        }

        if (target == null) {
            return number;
        }

        return number + extractor.apply(target).doubleValue();
    }

}
