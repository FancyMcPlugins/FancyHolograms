package de.oliver.fancyholograms.hologram.version;

import com.mojang.math.Transformation;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.events.HologramHideEvent;
import de.oliver.fancyholograms.api.events.HologramShowEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;

import static de.oliver.fancylib.ReflectionUtils.getValue;

public final class Hologram1_20_4 extends Hologram {

    @Nullable
    private Entity entity;

    public Hologram1_20_4(@NotNull final HologramData data) {
        super(data);
    }

    @Override
    public @Nullable org.bukkit.entity.Entity getEntity() {
        return entity != null ? entity.getBukkitEntity() : null;
    }

    @Override
    public void create() {
        final var location = data.getLocation();
        if (!location.isWorldLoaded()) {
            return; // no location data, cannot be created
        }

        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();

        switch (data.getType()) {
            case TEXT -> this.entity = new Display.TextDisplay(EntityType.TEXT_DISPLAY, world);
            case BLOCK -> this.entity = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, world);
            case ITEM -> this.entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, world);
            case DROPPED_ITEM -> {
                this.entity = new ItemEntity(EntityType.ITEM, world);
                this.entity.setNoGravity(true);
            }
        }

        if (this.entity instanceof Display display) {
            display.setTransformationInterpolationDuration(1);
            display.setTransformationInterpolationDelay(0);
        }

        update();
    }

    @Override
    public void delete() {
        this.entity = null;
    }

    @Override
    public void update() {
        final var entity = this.entity;
        if (entity == null) {
            return; // doesn't exist, nothing to update
        }

        // location data
        final var location = data.getLocation();
        if (location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        } else {
            entity.setPosRaw(location.x(), location.y(), location.z());
            entity.setYRot(location.getYaw());
            entity.setXRot(location.getPitch());
        }

        if (entity instanceof TextDisplay textDisplay && data instanceof TextHologramData textData) {
            // line width
            final var DATA_LINE_WIDTH_ID = ReflectionUtils.getStaticValue(TextDisplay.class, MappingKeys1_20_4.TEXT_DISPLAY__DATA_LINE_WIDTH_ID.getMapping());
            entity.getEntityData().set((EntityDataAccessor<Integer>) DATA_LINE_WIDTH_ID, Hologram.LINE_WIDTH);

            // background
            final var DATA_BACKGROUND_COLOR_ID = ReflectionUtils.getStaticValue(TextDisplay.class, MappingKeys1_20_4.TEXT_DISPLAY__DATA_BACKGROUND_COLOR_ID.getMapping());

            final var background = textData.getBackground();
            if (background == null) {
                entity.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, TextDisplay.INITIAL_BACKGROUND);
            } else if (background == Hologram.TRANSPARENT) {
                entity.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, 0);
            } else {
                entity.getEntityData().set((EntityDataAccessor<Integer>) DATA_BACKGROUND_COLOR_ID, background.asARGB());
            }

            // text shadow
            if (textData.hasTextShadow()) {
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

            // see through
            if (textData.isSeeThrough()) {
                textDisplay.setFlags((byte) (textDisplay.getFlags() | TextDisplay.FLAG_SEE_THROUGH));
            } else {
                textDisplay.setFlags((byte) (textDisplay.getFlags() & ~TextDisplay.FLAG_SEE_THROUGH));
            }

            if (textData.getTextAlignment() == org.bukkit.entity.TextDisplay.TextAlignment.RIGHT) {
                textDisplay.setFlags((byte) (textDisplay.getFlags() | TextDisplay.FLAG_ALIGN_RIGHT));
            } else {
                textDisplay.setFlags((byte) (textDisplay.getFlags() & ~TextDisplay.FLAG_ALIGN_RIGHT));
            }

        } else if (entity instanceof Display.ItemDisplay itemDisplay && data instanceof ItemHologramData itemData) {
            // item
            itemDisplay.setItemStack(ItemStack.fromBukkitCopy(itemData.getItemStack()));

        } else if (entity instanceof Display.BlockDisplay blockDisplay && data instanceof BlockHologramData blockData) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.of("minecraft:" + blockData.getBlock().name().toLowerCase(), ':'));
            blockDisplay.setBlockState(block.defaultBlockState());
        }

        if (entity instanceof Display display && data instanceof DisplayHologramData displayData) {
            // billboard data
            display.setBillboardConstraints(switch (displayData.getBillboard()) {
                case FIXED -> Display.BillboardConstraints.FIXED;
                case VERTICAL -> Display.BillboardConstraints.VERTICAL;
                case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
                case CENTER -> Display.BillboardConstraints.CENTER;
            });

            // brightness
            if (displayData.getBrightness() != null) {
                display.setBrightnessOverride(new Brightness(displayData.getBrightness().getBlockLight(), displayData.getBrightness().getSkyLight()));
            }

            // entity scale AND MORE!
            display.setTransformation(new Transformation(
                    displayData.getTranslation(),
                    new Quaternionf(),
                    displayData.getScale(),
                    new Quaternionf())
            );

            // entity shadow
            display.setShadowRadius(displayData.getShadowRadius());
            display.setShadowStrength(displayData.getShadowStrength());
        }
    }


    @Override
    public boolean show(@NotNull final Player player) {
        if (!new HologramShowEvent(this, player).callEvent()) {
            return false;
        }

        if (this.entity == null) {
            create(); // try to create it if it doesn't exist every time
        }

        final var entity = this.entity;
        if (entity == null) {
            return false; // could not be created, nothing to show
        }

        if (!data.getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
            return false;
        }

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        // TODO: cache player protocol version
        // TODO: fix this
//        final var protocolVersion = FancyHologramsPlugin.get().isUsingViaVersion() ? Via.getAPI().getPlayerVersion(player.getUniqueId()) : MINIMUM_PROTOCOL_VERSION;
//        if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
//            System.out.println("nope protocol");
//            return false;
//        }

        serverPlayer.connection.send(new ClientboundAddEntityPacket(entity));
        this.viewers.add(player.getUniqueId());
        refreshHologram(player);

        return true;
    }

    @Override
    public boolean hide(@NotNull final Player player) {
        if (!new HologramHideEvent(this, player).callEvent()) {
            return false;
        }

        final var entity = this.entity;
        if (entity == null) {
            return false; // doesn't exist, nothing to hide
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));

        this.viewers.remove(player.getUniqueId());
        return true;
    }


    @Override
    public void refresh(@NotNull final Player player) {
        final var entity = this.entity;
        if (entity == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!isViewer(player)) {
            return;
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundTeleportEntityPacket(entity));

        if (entity instanceof TextDisplay textDisplay) {
            textDisplay.setText(PaperAdventure.asVanilla(getShownText(player)));
        }

        final var values = new ArrayList<DataValue<?>>();

        //noinspection unchecked
        for (final var item : ((Int2ObjectMap<DataItem<?>>) getValue(entity.getEntityData(), "e")).values()) {
            values.add(item.value());
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetEntityDataPacket(entity.getId(), values));
    }

}
