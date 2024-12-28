package de.oliver.fancyholograms.api.trait;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
public interface HologramTraitRegistry {

    @ApiStatus.Experimental
    boolean register(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    boolean unregister(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    boolean isRegistered(Class<? extends HologramTrait> trait);

    @ApiStatus.Experimental
    List<Class<? extends HologramTrait>> getRegisteredTraits();
}
