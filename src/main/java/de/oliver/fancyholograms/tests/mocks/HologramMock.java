package de.oliver.fancyholograms.tests.mocks;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HologramMock extends Hologram {

    private final Runnable spawnToCallback;
    private final Runnable despawnFromCallback;
    private final Runnable updateForCallback;

    public HologramMock(@NotNull HologramData data, Runnable spawnToCallback, Runnable despawnFromCallback, Runnable updateForCallback) {
        super(data);
        this.spawnToCallback = spawnToCallback;
        this.despawnFromCallback = despawnFromCallback;
        this.updateForCallback = updateForCallback;
    }

    @Override
    public void spawnTo(@NotNull Player player) {
        spawnToCallback.run();
    }

    @Override
    public void despawnFrom(@NotNull Player player) {
        despawnFromCallback.run();
    }

    @Override
    public void updateFor(@NotNull Player player) {
        updateForCallback.run();
    }
}
