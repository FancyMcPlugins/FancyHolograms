package de.oliver.fancyholograms.api.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.HologramStackData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HologramStack extends Hologram {
    private final List<Hologram> holograms = new ArrayList<>();

    protected HologramStack(@NotNull HologramStackData data) {
        super(data);
    }

    @Override
    public @NotNull HologramStackData getData() {
        return (HologramStackData) this.data;
    }

    @Override
    public @Nullable Display getDisplayEntity() {
        return null;
    }

    @Override
    protected void create() {
        for (HologramData hologramData : this.getData().getContent()) {
            Hologram hologram = FancyHologramsPlugin.get().getHologramManager().create(hologramData);
            this.holograms.add(hologram);
        }
    }

    @Override
    protected void delete() {
        for (Hologram hologram : holograms) {
            hologram.delete();
        }
    }

    @Override
    protected void update() {
        HashMap<HologramData, Hologram> currHolograms = new HashMap<>();
        for (Hologram hologram : this.holograms) {
            currHolograms.put(hologram.getData(), hologram);
        }
        this.holograms.clear();

        for (HologramData hologramData : this.getData().getContent()) {
            Hologram hologram;
            if (currHolograms.containsKey(hologramData)) {
                hologram = currHolograms.get(hologramData);
                currHolograms.remove(hologramData);
            } else {
                hologram = FancyHologramsPlugin.get().getHologramManager().create(hologramData);
            }

            this.holograms.add(hologram);
        }

        // Handle old/removed holograms
        for (Hologram hologram : currHolograms.values()) {
            for (UUID viewer : hologram.getViewers()) {
                Player player = Bukkit.getPlayer(viewer);
                if (player != null) {
                    forceHideHologram(player);
                }
            }

            hologram.delete();
        }
    }

    @Override
    protected boolean show(@NotNull Player player) {
        boolean success = true;

        for (Hologram hologram : holograms) {
            if (!hologram.show(player)) {
                success = false;
            }
        }

        return success;
    }

    @Override
    protected boolean hide(@NotNull Player player) {
        boolean success = true;

        for (Hologram hologram : holograms) {
            if (!hologram.hide(player)) {
                success = false;
            }
        }

        return success;
    }

    @Override
    protected void refresh(@NotNull Player player) {
        for (Hologram hologram : holograms) {
            hologram.refresh(player);
        }
    }
}
