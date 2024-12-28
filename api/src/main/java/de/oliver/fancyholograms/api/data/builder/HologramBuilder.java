package de.oliver.fancyholograms.api.data.builder;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.property.Visibility;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

public abstract class HologramBuilder {

    protected DisplayHologramData data;

    private HologramBuilder() {
    }

    /**
     * Builds and returns a new Hologram instance using the current configuration
     * in the HologramBuilder.
     *
     * @return a new instance of Hologram created based on the configured data
     */
    public Hologram build() {
        return FancyHolograms.get().getHologramFactory().apply(data);
    }

    /**
     * Builds a new Hologram instance using the current configuration in the HologramBuilder
     * and registers it.
     *
     * @return a new instance of Hologram that has been registered with the registry
     */
    public Hologram buildAndRegister() {
        Hologram hologram = build();
        FancyHolograms.get().getRegistry().register(hologram);
        return hologram;
    }

    // The following methods are setters for the HologramData class

    public HologramBuilder visibilityDistance(int distance) {
        data.setVisibilityDistance(distance);
        return this;
    }

    public HologramBuilder visibility(Visibility visibility) {
        data.setVisibility(visibility);
        return this;
    }

    public HologramBuilder persistent(boolean persistent) {
        data.setPersistent(persistent);
        return this;
    }

    public HologramBuilder linkedNpcName(String linkedNpcName) {
        data.setLinkedNpcName(linkedNpcName);
        return this;
    }

    // The following methods are specific to the DisplayHologramData class

    public HologramBuilder billboard(Display.Billboard billboard) {
        data.setBillboard(billboard);
        return this;
    }

    public HologramBuilder scale(float x, float y, float z) {
        data.setScale(new Vector3f(x, y, z));
        return this;
    }

    public HologramBuilder translation(float x, float y, float z) {
        data.setTranslation(new Vector3f(x, y, z));
        return this;
    }

    public HologramBuilder brightness(int blockLight, int skyLight) {
        data.setBrightness(new Display.Brightness(blockLight, skyLight));
        return this;
    }

    public HologramBuilder shadowRadius(float shadowRadius) {
        data.setShadowRadius(shadowRadius);
        return this;
    }

    public HologramBuilder shadowStrength(float shadowStrength) {
        data.setShadowStrength(shadowStrength);
        return this;
    }

    public HologramBuilder interpolationDuration(int interpolationDuration) {
        data.setInterpolationDuration(interpolationDuration);
        return this;
    }
}
