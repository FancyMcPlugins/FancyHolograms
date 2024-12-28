package de.oliver.fancyholograms.tests.api;

import de.oliver.fancyholograms.api.HologramRegistry;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.registry.HologramRegistryImpl;
import de.oliver.fancyholograms.tests.mocks.HologramMock;
import de.oliver.fancylib.tests.annotations.FPBeforeEach;
import de.oliver.fancylib.tests.annotations.FPTest;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

import static de.oliver.fancylib.tests.Expectable.expect;

public class HologramRegistryTest {

    private HologramRegistry registry;

    @FPBeforeEach
    public void setUp(Player player) {
        registry = new HologramRegistryImpl();
    }

    @FPTest(name = "Register hologram to registry")
    public void testRegister(Player player) {
        Hologram hologram = new HologramMock(
                new TextHologramData("Test", new Location(null, 0, 0, 0)),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);

        Optional<Hologram> got = registry.get("Test");
        expect(got.isPresent()).toBe(true);
        expect(got.get()).toEqual(hologram);
    }

}
