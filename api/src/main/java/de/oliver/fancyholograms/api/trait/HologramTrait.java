package de.oliver.fancyholograms.api.trait;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.HologramController;
import de.oliver.fancyholograms.api.HologramRegistry;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents a trait that can be attached to a hologram. This class provides a structure for
 * managing the lifecycle of traits related to holograms. It defines methods to handle
 * initialization, attachment, updates, and data persistence.
 * <p>
 * Subclasses of this abstract class must implement the specific behavior of the trait by
 * overriding the provided lifecycle methods.
 */
@ApiStatus.Experimental
public abstract class HologramTrait {

    protected final String name;
    protected final FancyHolograms api = FancyHolograms.get();
    protected final ExtendedFancyLogger logger = api.getFancyLogger();
    protected final HologramController controller = api.getController();
    protected final HologramRegistry registry = api.getRegistry();
    protected final ScheduledExecutorService hologramThread = api.getHologramThread();
    protected Hologram hologram;


    /**
     * Creates a new hologram trait with the given name.
     * @param name the name of the trait
     */
    public HologramTrait(String name) {
        this.name = name;
    }

    public HologramTrait() {
        this.name = getClass().getSimpleName();
    }

    public void attachHologram(Hologram hologram) {
        if (this.hologram != null) {
            throw new IllegalStateException("Trait is already attached to a hologram");
        }

        this.hologram = hologram;
    }

    /**
     * Called when the trait is attached to a hologram.
     * The hologram is available at this point.
     */
    public void onAttach() {
    }

    /**
     * Called when the hologram is spawned to a player.
     */
    public void onSpawn(Player player) {
    }

    /**
     * Called when the hologram is despawned from a player.
     */
    public void onDespawn(Player player) {
    }

    /**
     * Called when the hologram is registered in the registry.
     */
    public void onRegister() {

    }

    /**
     * Called when the hologram is unregistered from the registry.
     */
    public void onUnregister() {
    }

    /**
     * Called when the hologram is being loaded.
     * In this method you should load all necessary data for the trait.
     */
    public void load() {
    }

    /**
     * Called when the hologram is being saved.
     * In this method you should save all necessary data for the trait.
     */
    public void save() {
    }

    public String getName() {
        return name;
    }

    public Hologram getHologram() {
        return hologram;
    }
}
