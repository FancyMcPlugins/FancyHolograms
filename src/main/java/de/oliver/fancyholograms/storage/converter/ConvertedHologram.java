package de.oliver.fancyholograms.storage.converter;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConvertedHologram {
    private final @NotNull String baseHologramId;
    private final @NotNull Location baseHologramLocation;
    private final @Nullable CommandSender audience;
    private final @NotNull List<HologramData> hologramParts = new ArrayList<>();

    public ConvertedHologram(@NotNull String baseHologramId, @NotNull Location baseHologramLocation, @Nullable CommandSender audience) {
        this.baseHologramId = baseHologramId;
        this.baseHologramLocation = baseHologramLocation;
        this.audience = audience;
    }

    public TextHologramData createTextHologram(@NotNull List<String> lines) {
        final TextHologramData hologram = new TextHologramData(getNextHologramId(), baseHologramLocation);

        logInfo(String.format("%s + Text Hologram (%s) with %s lines", baseHologramId, hologram.getName(), lines.size()));

        hologram.setText(lines);
        hologram.setTextShadow(true);
        hologram.setBillboard(Display.Billboard.VERTICAL);

        return hologram;
    }

    public ItemHologramData createItemHologram(@NotNull ItemStack item) {
        final ItemHologramData hologram = new ItemHologramData(getNextHologramId(), baseHologramLocation);

        logInfo(String.format("%s + Item Hologram (%s) with type %s", baseHologramId, hologram.getName(), item.getType().name()));

        hologram.setItemStack(item);
        hologram.setBillboard(Display.Billboard.VERTICAL);

        return hologram;
    }

    public @NotNull List<HologramData> getHologramParts() {
        return this.hologramParts;
    }

    private @NotNull String getNextHologramId() {
        if (hologramParts.isEmpty()) {
            return baseHologramId;
        }

        return String.format("%s_%s", baseHologramId, hologramParts.size());
    }

    public void logInfo(@NotNull String info) {
        if (audience == null) return;

        MessageHelper.send(audience, info);
    }

}
