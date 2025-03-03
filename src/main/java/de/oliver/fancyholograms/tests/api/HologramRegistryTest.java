package de.oliver.fancyholograms.tests.api;

import de.oliver.fancyholograms.api.HologramRegistry;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.registry.HologramRegistryImpl;
import de.oliver.fancyholograms.tests.mocks.HologramMock;
import de.oliver.fancylib.tests.annotations.FPBeforeEach;
import de.oliver.fancylib.tests.annotations.FPTest;
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
                new TextHologramData("Test", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);

        Optional<Hologram> got = registry.get("Test");
        expect(got.isPresent()).toBe(true);
        expect(got.get()).toEqual(hologram);
    }

    @FPTest(name = "Unregister hologram from registry")
    public void testUnregister(Player player) {
        Hologram hologram = new HologramMock(
                new TextHologramData("Test", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);
        expect(registry.get("Test").isPresent()).toBe(true);

        registry.unregister(hologram);

        Optional<Hologram> got = registry.get("Test");
        expect(got.isPresent()).toBe(false);
    }

    @FPTest(name = "Check if hologram exists in registry")
    public void testContains(Player player) {
        Hologram hologram = new HologramMock(
                new TextHologramData("Test", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);
        expect(registry.contains("Test")).toBe(true);
    }

    @FPTest(name = "Retrieve hologram from registry")
    public void testGet(Player player) {
        Hologram hologram = new HologramMock(
                new TextHologramData("Test", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);

        Optional<Hologram> got = registry.get("Test");
        expect(got.isPresent()).toBe(true);
        expect(got.get()).toEqual(hologram);
    }

    @FPTest(name = "Retrieve all holograms from registry")
    public void testGetAll(Player player) {
        Hologram hologram1 = new HologramMock(
                new TextHologramData("Test1", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );
        Hologram hologram2 = new HologramMock(
                new TextHologramData("Test2", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram1);
        registry.register(hologram2);

        expect(registry.getAll().size()).toBe(2);
        expect(registry.getAll().contains(hologram1)).toBe(true);
        expect(registry.getAll().contains(hologram2)).toBe(true);
    }

    @FPTest(name = "Retrieve all persistent holograms from registry")
    public void testGetAllPersistent(Player player) {
        Hologram hologram1 = new HologramMock(
                new TextHologramData("Test1", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        TextHologramData data2 = new TextHologramData("Test2", player.getLocation());
        data2.setPersistent(false);
        Hologram hologram2 = new HologramMock(
                data2,
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram1);
        registry.register(hologram2);

        expect(registry.getAllPersistent()).toHaveLength(1);
        expect(registry.getAllPersistent().contains(hologram1)).toBe(true);
        expect(registry.getAllPersistent().contains(hologram2)).toBe(false);
    }

    @FPTest(name = "Retrieve hologram from registry, ensuring it exists")
    public void testMustGet(Player player) {
        Hologram hologram = new HologramMock(
                new TextHologramData("Test", player.getLocation()),
                () -> {},
                () -> {},
                () -> {}
            );

        registry.register(hologram);

        expect(registry.mustGet("Test")).toBeDefined();

        try {
            registry.mustGet("NonExistent");
        } catch (IllegalArgumentException e) {
            expect(e).toBeDefined();
        }
    }
}
