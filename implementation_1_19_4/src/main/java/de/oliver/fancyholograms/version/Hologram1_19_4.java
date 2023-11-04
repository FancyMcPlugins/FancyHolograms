package de.oliver.fancyholograms.version;

import com.mojang.math.Transformation;
import com.viaversion.viaversion.api.Via;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancylib.ReflectionUtils;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
    private Display display;

    public Hologram1_19_4(@NotNull final HologramData data) {
        super(data);
    }

    @Override
    public void create() {
        final var location = data.getDisplayData().getLocation();
        if (location == null || location.getWorld() == null) {
            return; // no location data, cannot be created
        }

        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();

        switch (data.getType()) {
            case TEXT -> this.display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, world);
            case BLOCK -> this.display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, world);
            case ITEM -> this.display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, world);
        }

        final var DATA_INTERPOLATION_DURATION_ID = ReflectionUtils.getStaticValue(Display.class, MappingKeys.DATA_INTERPOLATION_DURATION_ID.getMapping());
        display.getEntityData().set((EntityDataAccessor<Integer>) DATA_INTERPOLATION_DURATION_ID, 1);

        final var DATA_INTERPOLATION_START_DELTA_TICKS_ID = ReflectionUtils.getStaticValue(Display.class, MappingKeys.DATA_INTERPOLATION_START_DELTA_TICKS_ID.getMapping());
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

        // location data
        final var location = data.getDisplayData().getLocation();
        if (location == null || location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        } else {
            display.setPosRaw(location.x(), location.y(), location.z());
            display.setYRot(location.getYaw());
            display.setXRot(location.getPitch());
        }


        // billboard data
        display.setBillboardConstraints(switch (data.getDisplayData().getBillboard()) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case CENTER -> Display.BillboardConstraints.CENTER;
        });


        if (display instanceof TextDisplay textDisplay && data.getTypeData() instanceof TextHologramData textData) {
            // line width
            final var DATA_LINE_WIDTH_ID = ReflectionUtils.getStaticValue(TextDisplay.class, MappingKeys.DATA_LINE_WIDTH_ID.getMapping());
            display.getEntityData().set((EntityDataAccessor<Integer>) DATA_LINE_WIDTH_ID, Hologram.LINE_WIDTH);

            // background
            final var DATA_BACKGROUND_COLOR_ID = ReflectionUtils.getStaticValue(TextDisplay.class, MappingKeys.DATA_BACKGROUND_COLOR_ID.getMapping());

            final var background = textData.getBackground();
            if (background == null) {
                display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, TextDisplay.INITIAL_BACKGROUND);
            } else if (background == Hologram.TRANSPARENT) {
                display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, 0);
            } else {
                display.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, background.value() | 0xC8000000);
            }

            // text shadow
            if (textData.isTextShadow()) {
                textDisplay.setFlags((byte) (textDisplay.getFlags() | TextDisplay.FLAG_SHADOW));
            } else {
                textDisplay.setFlags((byte) (textDisplay.getFlags() & ~TextDisplay.FLAG_SHADOW));
            }

            // text alignment
            if (textData.getTextAlignment() == org.bukkit.entity.TextDisplay.TextAlignment.LEFT) {
                textDisplay.setFlags((byte) (textDisplay.getFlags() | TextDisplay.FLAG_ALIGN_LEFT));
            } else {
                textDisplay.setFlags((byte) (textDisplay.getFlags() & ~TextDisplay.FLAG_ALIGN_LEFT));
            }

            if (textData.getTextAlignment() == org.bukkit.entity.TextDisplay.TextAlignment.RIGHT) {
                textDisplay.setFlags((byte) (textDisplay.getFlags() | TextDisplay.FLAG_ALIGN_RIGHT));
            } else {
                textDisplay.setFlags((byte) (textDisplay.getFlags() & ~TextDisplay.FLAG_ALIGN_RIGHT));
            }

        } else if (display instanceof Display.ItemDisplay itemDisplay && data.getTypeData() instanceof ItemHologramData itemData) {
            // item
            itemDisplay.setItemStack(ItemStack.fromBukkitCopy(itemData.getItem()));

        } else if (display instanceof Display.BlockDisplay blockDisplay && data.getTypeData() instanceof BlockHologramData blockData) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.of("minecraft:" + blockData.getBlock().name().toLowerCase(), ':'));
            blockDisplay.setBlockState(block.defaultBlockState());
        }

        // brightness
        if (data.getDisplayData().getBrightness() != null) {
            display.setBrightnessOverride(new Brightness(data.getDisplayData().getBrightness().getBlockLight(),
                    data.getDisplayData().getBrightness().getSkyLight()));
        }

        // entity scale AND MORE!
        display.setTransformation(new Transformation(
                data.getDisplayData().getTranslation(),
                new Quaternionf(),
                data.getDisplayData().getScale(),
                new Quaternionf())
        );


        // entity shadow
        display.setShadowRadius(data.getDisplayData().getShadowRadius());
        display.setShadowStrength(data.getDisplayData().getShadowStrength());
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

        if (!data.getDisplayData().getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
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

        if (display instanceof TextDisplay textDisplay) {
            textDisplay.setText(PaperAdventure.asVanilla(getShownText(player)));
        }

        final var values = new ArrayList<DataValue<?>>();

        //noinspection unchecked
        for (final var item : ((Int2ObjectMap<DataItem<?>>) getValue(display.getEntityData(), "e")).values()) {
            values.add(item.value());
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetEntityDataPacket(display.getId(), values));
    }

}
