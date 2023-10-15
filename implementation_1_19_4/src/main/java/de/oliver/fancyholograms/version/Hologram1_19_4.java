package de.oliver.fancyholograms.version;

import com.mojang.math.Transformation;
import com.viaversion.viaversion.api.Via;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancylib.ReflectionUtils;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;

import static de.oliver.fancylib.ReflectionUtils.getValue;

public final class Hologram1_19_4 extends Hologram {

    @Nullable
    private TextDisplay display;


    public Hologram1_19_4(@NotNull final HologramData data) {
        super(data);
    }


    @Override
    public void create() {
        final var location = getData().getLocation();
        if (location == null || location.getWorld() == null) {
            return; // no location data, cannot be created
        }

        this.display = new TextDisplay(EntityType.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle());

        final var DATA_INTERPOLATION_DURATION_ID = ReflectionUtils.getStaticValue(Display.class, "r"); //DATA_INTERPOLATION_DURATION_ID
        display.getEntityData().set((EntityDataAccessor<Integer>) DATA_INTERPOLATION_DURATION_ID, 1);

        final var DATA_INTERPOLATION_START_DELTA_TICKS_ID = ReflectionUtils.getStaticValue(Display.class, "q"); //DATA_INTERPOLATION_START_DELTA_TICKS_ID
        display.getEntityData().set((EntityDataAccessor<Integer>) DATA_INTERPOLATION_START_DELTA_TICKS_ID, 0);

        updateHologram();
    }

    @Override
    public void delete() {
        this.display = null;
    }

    @Override
    public void update() {
        final var display = this.display;
        if (display == null) {
            return; // doesn't exist, nothing to update
        }

        // initial data
        final var DATA_LINE_WIDTH_ID = ReflectionUtils.getStaticValue(TextDisplay.class, "aL"); //DATA_LINE_WIDTH_ID
        display.getEntityData().set((EntityDataAccessor<Integer>) DATA_LINE_WIDTH_ID, Hologram.LINE_WIDTH);


        // location data
        final var location = getData().getLocation();
        if (location == null || location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        } else {
            display.setPosRaw(location.x(), location.y(), location.z());
            display.setYRot(location.getYaw());
            display.setXRot(location.getPitch());
        }


        // billboard data
        display.setBillboardConstraints(switch (getData().getBillboard()) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case CENTER -> Display.BillboardConstraints.CENTER;
        });


        // background
        final var DATA_BACKGROUND_COLOR_ID = ReflectionUtils.getStaticValue(TextDisplay.class, "aM"); //DATA_BACKGROUND_COLOR_ID

        final var background = getData().getBackground();
        if (background == null) {
            display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, TextDisplay.INITIAL_BACKGROUND);
        } else if (background == Hologram.TRANSPARENT) {
            display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, 0);
        } else {
            display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, background.value() | 0xC8000000);
        }

        if (getData().getBrightness() != null) {
            display.setBrightnessOverride(new Brightness(getData().getBrightness().getBlockLight(),
                    getData().getBrightness().getSkyLight()));
        }

        // entity scale AND MORE!
        display.setTransformation(new Transformation(
                getData().getTranslation(),
                new Quaternionf(),
                getData().getScale(),
                new Quaternionf())
        );


        // entity shadow
        display.setShadowRadius(getData().getShadowRadius());
        display.setShadowStrength(getData().getShadowStrength());


        // text shadow
        if (getData().isTextHasShadow()) {
            display.setFlags((byte) (display.getFlags() | TextDisplay.FLAG_SHADOW));
        } else {
            display.setFlags((byte) (display.getFlags() & ~TextDisplay.FLAG_SHADOW));
        }

        // text alignment
        if (getData().getTextAlignment() == org.bukkit.entity.TextDisplay.TextAlignment.LEFT) {
            display.setFlags((byte) (display.getFlags() | TextDisplay.FLAG_ALIGN_LEFT));
        } else {
            display.setFlags((byte) (display.getFlags() & ~TextDisplay.FLAG_ALIGN_LEFT));
        }

        if (getData().getTextAlignment() == org.bukkit.entity.TextDisplay.TextAlignment.RIGHT) {
            display.setFlags((byte) (display.getFlags() | TextDisplay.FLAG_ALIGN_RIGHT));
        } else {
            display.setFlags((byte) (display.getFlags() & ~TextDisplay.FLAG_ALIGN_RIGHT));
        }
    }


    @Override
    public boolean show(@NotNull final Player player) {
        if (this.display == null) {
            create(); // try to create it if it doesn't exist every time
        }

        final var display = this.display;
        if (display == null) {
            return false; // could not be created, nothing to show
        }

        if (!data.getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
            return false;
        }

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        // TODO: cache player protocol version
        final var protocolVersion = FancyHologramsPlugin.get().isUsingViaVersion() ? Via.getAPI().getPlayerVersion(player) : MINIMUM_PROTOCOL_VERSION;
        if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
            return false;
        }

        serverPlayer.connection.send(new ClientboundAddEntityPacket(display));
        this.shown.add(player.getUniqueId());
        refreshHologram(player);

        return true;
    }

    @Override
    public boolean hide(@NotNull final Player player) {
        final var display = this.display;
        if (display == null) {
            return false; // doesn't exist, nothing to hide
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(display.getId()));

        return true;
    }


    @Override
    public void refresh(@NotNull final Player player) {
        final var display = this.display;
        if (display == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!isShown(player)) {
            return;
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundTeleportEntityPacket(display));

        display.setText(PaperAdventure.asVanilla(getShownText(player)));

        final var values = new ArrayList<DataValue<?>>();

        //noinspection unchecked
        for (final var item : ((Int2ObjectMap<DataItem<?>>) getValue(display.getEntityData(), "e")).values()) {
            values.add(item.value());
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetEntityDataPacket(display.getId(), values));
    }

}
