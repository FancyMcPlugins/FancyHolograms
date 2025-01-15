package de.oliver.fancyholograms.api.hologram;

import com.google.common.collect.Sets;
import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.trait.HologramTrait;
import de.oliver.fancyholograms.api.trait.HologramTraitTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * This class provides core functionalities for managing viewers, spawning, despawning, and updating holograms.
 */
public abstract class Hologram {

    public static final int LINE_WIDTH = 1000;
    public static final Color TRANSPARENT = Color.fromARGB(0);
    protected static final int MINIMUM_PROTOCOL_VERSION = 762;

    protected final @NotNull HologramData data;
    protected final @NotNull Set<UUID> viewers;
    protected final @NotNull HologramTraitTrait traitTrait;

    protected Hologram(@NotNull final HologramData data) {
        this.data = data;
        this.viewers = new HashSet<>();
        this.traitTrait = new HologramTraitTrait(this);
    }

    /**
     * Forcefully spawns the hologram and makes it visible to the specified player.
     *
     * @param player the player to whom the hologram should be shown; must not be null
     */
    @ApiStatus.Internal
    public abstract void spawnTo(@NotNull final Player player);

    /**
     * Forcefully despawns the hologram and makes it invisible to the specified player.
     *
     * @param player the player from whom the hologram should be hidden; must not be null
     */
    @ApiStatus.Internal
    public abstract void despawnFrom(@NotNull final Player player);

    /**
     * Updates the hologram for the specified player.
     *
     * @param player the player for whom the hologram should be updated; must not be null
     */
    @ApiStatus.Internal
    public abstract void updateFor(@NotNull final Player player);


    /**
     * @return a copy of the set of UUIDs of players currently viewing the hologram
     */
    public final @NotNull Set<UUID> getViewers() {
        return Sets.newHashSet(this.viewers);
    }

    @ApiStatus.Internal
    public void setViewers(@NotNull final Set<UUID> viewers) {
        this.viewers.clear();
        this.viewers.addAll(viewers);
    }

    @ApiStatus.Internal
    public void removeViewer(@NotNull final UUID viewer) {
        this.viewers.remove(viewer);
    }

    /**
     * @param player the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final Player player) {
        return isViewer(player.getUniqueId());
    }

    /**
     * @param player the uuid of the player to check for
     * @return whether the player is currently viewing the hologram
     */
    public final boolean isViewer(@NotNull final UUID player) {
        return this.viewers.contains(player);
    }

    @ApiStatus.Experimental
    public @NotNull HologramTraitTrait getTraitTrait() {
        return traitTrait;
    }

    @ApiStatus.Experimental
    public HologramData addTrait(HologramTrait trait) {
        traitTrait.addTrait(trait);
        return data;
    }

    @ApiStatus.Experimental
    public HologramData addTrait(Class<? extends HologramTrait> traitClass) {
        HologramTrait trait = null;
        try {
            trait = traitClass.getConstructor(null).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to instantiate trait " + traitClass.getSimpleName());
            FancyHolograms.get().getFancyLogger().error(e);
        }

        traitTrait.addTrait(trait);
        return data;
    }

    public final @NotNull HologramData getData() {
        return this.data;
    }

    /**
     * Retrieves the data associated with the hologram and casts it to the specified type.
     *
     * @param <T>   the type of {@code HologramData} to retrieve
     * @param clazz the class of the data type to retrieve; must not be null
     * @return the hologram data cast to the specified type
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> @NotNull T getData(@NotNull Class<T> clazz) {
        return clazz.cast(this.data);
    }

    /**
     * Retrieves the data associated with the hologram, if it can be cast to the specified type.
     *
     * @param <T>   the type of {@code HologramData}
     * @param clazz the class of the data type to retrieve; must not be null
     * @return the hologram data cast to the specified type, or null if the cast fails
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> @Nullable T getDataNullable(@NotNull Class<T> clazz) {
        try {
            return clazz.cast(this.data);
        } catch (ClassCastException ignored) {
            return null;
        }
    }

    /**
     * Consumes the data associated with the hologram if it can be cast to the specified type.
     *
     * @param <T>      the type of {@link HologramData} to consume
     * @param clazz    the class of the data type to consume; must not be null
     * @param consumer the action to perform with the consumed data; must not be null
     */
    @ApiStatus.Experimental
    public final <T extends HologramData> void consumeData(@NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
        final T data = getDataNullable(clazz);

        if (data != null) {
            consumer.accept(data);
        }
    }

    /**
     * Gets the text shown in the hologram. If a player is specified, placeholders in the text are replaced
     * with their corresponding values for the player.
     *
     * @param player the player to get the placeholders for, or null if no placeholders should be replaced
     * @return the text shown in the hologram
     */
    public final Component getShownText(@Nullable final Player player) {
        if (!(getData() instanceof TextHologramData textData)) {
            return null;
        }

        var text = String.join("\n", textData.getText());

        return ModernChatColorHandler.translate(text, player);
    }
}
