package de.oliver.fancyholograms.api.trait;

import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HologramTraitTrait extends HologramTrait {

    private final List<HologramTrait> traits;

    public HologramTraitTrait(Hologram hologram) {
        super("trait");
        attachHologram(hologram);
        this.traits = new ArrayList<>();
    }

    public void addTrait(HologramTrait trait) {
        this.traits.add(trait);
        trait.attachHologram(hologram);
        trait.onAttach();
    }

    @Override
    public void onAttach() {
        List<Class<? extends HologramTrait>> registeredTraits = api.getTraitRegistry().getRegisteredTraits();
        for (Class<? extends HologramTrait> traitClass : registeredTraits) {
            if (!traitClass.isAnnotationPresent(DefaultTrait.class)) {
                continue;
            }

            try {
                HologramTrait trait = traitClass.getConstructor().newInstance();
                this.traits.add(trait);
                logger.debug("Attached default trait " + traitClass.getName() + " to hologram " + hologram.getData().getName());
                trait.onAttach();
            } catch (Exception e) {
                logger.error("Failed to instantiate trait " + traitClass.getName());
                logger.error(e);
            }
        }
    }

    @Override
    public void onSpawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onSpawn(player);
        }
    }

    @Override
    public void onDespawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onDespawn(player);
        }
    }

    @Override
    public void onRegister() {
        for (HologramTrait trait : this.traits) {
            trait.onRegister();
        }
    }

    @Override
    public void onUnregister() {
        for (HologramTrait trait : this.traits) {
            trait.onUnregister();
        }
    }

    @Override
    public void load() {
        for (HologramTrait trait : this.traits) {
            trait.load();
        }
    }

    @Override
    public void save() {
        for (HologramTrait trait : this.traits) {
            trait.save();
        }
    }
}
