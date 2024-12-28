package de.oliver.fancyholograms.trait;

import de.oliver.fancyholograms.api.trait.HologramTrait;
import de.oliver.fancyholograms.api.trait.HologramTraitRegistry;

import java.util.List;

public class HologramTraitRegistryImpl implements HologramTraitRegistry {


    @Override
    public boolean register(Class<? extends HologramTrait> trait) {
        return false;
    }

    @Override
    public boolean unregister(Class<? extends HologramTrait> trait) {
        return false;
    }

    @Override
    public boolean isRegistered(Class<? extends HologramTrait> trait) {
        return false;
    }

    @Override
    public List<Class<? extends HologramTrait>> getRegisteredTraits() {
        return List.of();
    }
}
