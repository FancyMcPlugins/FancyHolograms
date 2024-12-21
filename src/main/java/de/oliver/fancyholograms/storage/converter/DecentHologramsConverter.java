package de.oliver.fancyholograms.storage.converter;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.File;
import java.util.*;

public class DecentHologramsConverter implements HologramConverter {
    private static final float VANILLA_PIXEL_BLOCK_SIZE = 0.0625f;
    private static final float TEXT_DISPLAY_PIXEL = VANILLA_PIXEL_BLOCK_SIZE / 3;
    private static final float TEXT_DISPLAY_LINE_HEIGHT = TEXT_DISPLAY_PIXEL * 14;
    private static final String PROCESS_ICONS_FLAG = "--processIcons";
    private static final File DECENT_HOLOGRAMS_DATA = new File("./plugins/DecentHolograms/holograms/");

    @Override
    public boolean canRunConverter() {
        return DECENT_HOLOGRAMS_DATA.exists();
    }

    @Override
    public @NotNull List<HologramData> convert(@NotNull HologramConversionSession spec) {
        boolean processIcons = Arrays.stream(spec.getAdditionalArguments()).anyMatch((arg) -> arg.equalsIgnoreCase(PROCESS_ICONS_FLAG));

        final List<String> targetHolograms = getConvertableHolograms()
            .stream()
            .filter((id) -> spec.getTarget().matches(id))
            .toList();

        if (targetHolograms.isEmpty()) {
            throw new RuntimeException("The provided target matches no holograms.");
        }

        ArrayList<HologramData> converted = new ArrayList<>();

        for (final String id : targetHolograms) {
            final List<HologramData> results = convert(id, processIcons);

            if (results.isEmpty()) {
                spec.logUnsuccessfulConversion(id, "Unable to convert this hologram, there is no convertable content.");
            } else {
                spec.logSuccessfulConversion(id, results);
            }

            converted.addAll(results);
        }

        return converted;
    }

    @Override
    public @NotNull List<String> getConvertableHolograms() {
        final File[] files = DECENT_HOLOGRAMS_DATA.listFiles();

        if (files == null || files.length == 0) {
            throw new RuntimeException("DecentHolograms holograms folder doesn't exist or is empty.");
        }

        return Arrays.stream(files)
            .map((file) -> file.getName().replace(".yml", ""))
            .toList();
    }

    private @NotNull List<HologramData> convert(@NotNull String hologramId, boolean processIcons) {
        final File file = DECENT_HOLOGRAMS_DATA.toPath()
            .resolve(hologramId.endsWith(".yml") ? hologramId : hologramId + ".yml")
            .toFile();

        if (!file.exists() || !file.canRead()) {
            throw new RuntimeException("File does not exist or is not readable.");
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        Objects.requireNonNull(data, "No data could be read from the DecentHolograms file!");

        final Location location = parseLocation(data.getString("location"));
        final double displayRange = data.getDouble("display-range");
        final int updateInterval = data.getInt("update-interval");

        // TODO handle exceptions here
        final Object firstPage = data.getMapList("pages")
            .getFirst()
            .get("lines");

        Objects.requireNonNull(firstPage, "There is no first page for that hologram.");

        final List<Map<String, ?>> firstPageSections = ((List<Map<String, ?>>) firstPage);

        final List<String> lines = firstPageSections
            .stream()
            .map((line) -> (String) line.get("content"))
            .toList();

        final TextHologramData hologram = new TextHologramData(hologramId, location);

        hologram.setText(lines);
        hologram.setTextShadow(true);
        hologram.setTextUpdateInterval(updateInterval);
        hologram.setVisibilityDistance((int) displayRange);
        hologram.setBillboard(Display.Billboard.VERTICAL);
        hologram.setPersistent(true);

        List<HologramData> results = new ArrayList<>();
        if (processIcons) {
            results.addAll(convertSplitLines(hologram, firstPageSections));
        } else {
            results.add(hologram);
        }

        return results;
    }

    @Deprecated
    private @NotNull List<HologramData> convertSplitLines(@NotNull TextHologramData base, @NotNull List<Map<String, ?>> lines) {
        final List<HologramData> stack = new ArrayList<>();
        final List<String> finalBaseLines = new ArrayList<>();
        stack.add(base);

        int subTypes = 0;
        float currentYOffset = 0f;

        // Track total height of hologram apx (hologram y is inverted)
        float totalHeight = 0f;

        for (final Map<String, ?> entry : lines) {
            final Object contentEntry = entry.get("content");
            // TODO add height

            if (!(contentEntry instanceof String line)) continue;

            if (line.startsWith("#ICON: ")) {
                final String materialTypeString = line.replace("#ICON: ", "");
                final Material material = Material.valueOf(materialTypeString);

                final String formattedId = String.format("%s_icon_%s", base.getName(), subTypes++);
                final ItemHologramData data = new ItemHologramData(formattedId, base.getLocation());

                data.setItemStack(new ItemStack(material));
                data.setBillboard(Display.Billboard.VERTICAL);
                data.setScale(new Vector3f(0.45f, 0.45f, 0.45f));
                data.setTranslation(new Vector3f(0f, currentYOffset, 0f));
                data.setVisibilityDistance(base.getVisibilityDistance());
                data.setPersistent(true);

                float h = TEXT_DISPLAY_LINE_HEIGHT + 0.12f;

                currentYOffset += h;
                totalHeight += h;

                // Empty space for text
                // TODO find average item height
                finalBaseLines.addAll(List.of("&r", "&r"));
                stack.add(data);
            } else {
                // Empty line
                finalBaseLines.add(line);
                float h = TEXT_DISPLAY_LINE_HEIGHT;

                currentYOffset += h;
                totalHeight += h;
            }
        }

        base.setText(finalBaseLines);

        // Now invert their y offset
        for (@NotNull HologramData holo : stack) {
            if (holo instanceof ItemHologramData itemHolo) {
                itemHolo.setTranslation(
                    new Vector3f(
                        itemHolo.getTranslation().x,
                        totalHeight - itemHolo.getTranslation().y - 0.25f,
                        itemHolo.getTranslation().z
                    )
                );
            }
        }

        return stack;
    }

    private @NotNull Location parseLocation(@Nullable String location) {
        Objects.requireNonNull(location, "Location cannot be empty!");

        final String[] split = location.split(":");

        if (split.length != 4) {
            throw new IllegalStateException(String.format("Location in %s didn't have 4 arguments (split by :)", location));
        }

        World world = Objects.requireNonNull(Bukkit.getWorld(split[0]), String.format("World does not exist for location %s", location));

        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);

        return new Location(world, x, y, z);
    }
}
