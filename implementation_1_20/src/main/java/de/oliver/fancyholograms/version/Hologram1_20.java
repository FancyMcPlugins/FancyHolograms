package de.oliver.fancyholograms.version;

import com.mojang.math.Transformation;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

import static de.oliver.fancylib.ReflectionUtils.getValue;

public final class Hologram1_20 extends Hologram {

    @Nullable
    private TextDisplay display;


    public Hologram1_20(@NotNull final HologramData data) {
        super(data);
    }


    @Override
    public void create() {
        final var location = getData().getLocation();
        if (location == null || location.getWorld() == null) {
            return; // no location data, cannot be created
        }

        this.display = new TextDisplay(EntityType.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle());

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
        display.setLineWidth(Hologram.LINE_WIDTH);


        // location data
        final var location = getData().getLocation();
        if (location == null || location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        } else {
            display.setPosRaw(location.x(), location.y(), location.z());
            display.setYRot(location.getYaw());
        }


        // billboard data
        display.setBillboardConstraints(switch (getData().getBillboard()) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case CENTER -> Display.BillboardConstraints.CENTER;
        });


        // background
        final var background = getData().getBackground();
        if (background == null) {
            display.setBackgroundColor(TextDisplay.INITIAL_BACKGROUND);
        } else if (background == Hologram.TRANSPARENT) {
            display.setBackgroundColor(0);
        } else {
            display.setBackgroundColor(background.value() | 0xC8000000);
        }

        // entity scale
        display.setTransformation(new Transformation(new Vector3f(),
                                                     new Quaternionf(),
                                                     new Vector3f(getData().getScale(),
                                                                  getData().getScale(),
                                                                  getData().getScale()),
                                                     new Quaternionf()));


        // entity shadow
        display.setShadowRadius(getData().getShadowRadius());
        display.setShadowStrength(getData().getShadowStrength());


        // text shadow
        if (getData().isTextHasShadow()) {
            display.setFlags((byte) (display.getFlags() | TextDisplay.FLAG_SHADOW));
        } else {
            display.setFlags((byte) (display.getFlags() & ~TextDisplay.FLAG_SHADOW));
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

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundAddEntityPacket(display));
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
