package de.oliver.fancyholograms.tests.api;

import de.oliver.fancyholograms.api.HologramController;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.controller.HologramControllerImpl;
import de.oliver.fancyholograms.tests.mocks.HologramMock;
import de.oliver.fancylib.tests.annotations.FPBeforeEach;
import de.oliver.fancylib.tests.annotations.FPTest;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static de.oliver.fancylib.tests.Expectable.expect;

public class HologramControllerTest {

    private HologramController controller;
    private Hologram hologram;

    private int spawnTo;
    private int despawnFrom;

    @FPBeforeEach
    public void setUp(Player player) {
        this.controller = new HologramControllerImpl();

        this.hologram = new HologramMock(
                new TextHologramData("Test", player.getLocation().clone()),
                () -> spawnTo++,
                () -> despawnFrom++,
                () -> {}
        );

        this.spawnTo = 0;
        this.despawnFrom = 0;
    }

    @FPTest(name = "Test showHologramTo")
    public void testShowHologramTo(Player player) {
        controller.showHologramTo(hologram, player);
        expect(spawnTo).toBe(1);
        expect(hologram.isViewer(player)).toBe(true);

        controller.showHologramTo(hologram, player);
        expect(spawnTo).toBe(1); // Should not spawn again
    }

    @FPTest(name = "Test hideHologramFrom")
    public void testHideHologramFrom(Player player) {
        controller.showHologramTo(hologram, player);
        expect(spawnTo).toBe(1);
        expect(hologram.isViewer(player)).toBe(true);


        hologram.getData().setLocation(new Location(player.getWorld(),0,10000, 0));

        controller.hideHologramFrom(hologram, player);
        expect(despawnFrom).toBe(1);
        expect(hologram.isViewer(player)).toBe(false);

        controller.hideHologramFrom(hologram, player);
        expect(despawnFrom).toBe(1); // Should not despawn again
    }

}
