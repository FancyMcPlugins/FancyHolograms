package de.oliver.fancyholograms.hologram.version;

import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.events.HologramHideEvent;
import de.oliver.fancyholograms.api.events.HologramShowEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancysitula.api.entities.*;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public final class HologramImpl extends Hologram {

    private FS_Display fsDisplay;

    public HologramImpl(@NotNull final HologramData data) {
        super(data);
    }

    @Override
    public void create() {
        final var location = data.getLocation();
        if (!location.isWorldLoaded()) {
            return;
        }

        switch (data.getType()) {
            case TEXT -> this.fsDisplay = new FS_TextDisplay();
            case ITEM -> this.fsDisplay = new FS_ItemDisplay();
            case BLOCK -> this.fsDisplay = new FS_BlockDisplay();
        }

        if (data instanceof DisplayHologramData dd) {
            fsDisplay.setTransformationInterpolationDuration(dd.getInterpolationDuration());
            fsDisplay.setTransformationInterpolationStartDeltaTicks(0);
        }

        update();
    }

    @Override
    public void delete() {
        this.fsDisplay = null;
    }

    @Override
    public void update() {
        if (fsDisplay == null) {
            return;
        }

        // location data
        final var location = data.getLocation();
        if (location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        }
        fsDisplay.setLocation(location);

        if (fsDisplay instanceof FS_TextDisplay textDisplay && data instanceof TextHologramData textData) {
            // line width
            textDisplay.setLineWidth(Hologram.LINE_WIDTH);

            // background
            final var background = textData.getBackground();
            if (background == null) {
                textDisplay.setBackground(1073741824); // default background
            } else if (background == Hologram.TRANSPARENT) {
                textDisplay.setBackground(0);
            } else {
                textDisplay.setBackground(background.asARGB());
            }

            textDisplay.setStyleFlags((byte) 0);
            textDisplay.setShadow(textData.hasTextShadow());
            textDisplay.setSeeThrough(textData.isSeeThrough());

            switch (textData.getTextAlignment()) {
                case LEFT -> textDisplay.setAlignLeft(true);
                case RIGHT -> textDisplay.setAlignRight(true);
                case CENTER -> {
                    textDisplay.setAlignLeft(false);
                    textDisplay.setAlignRight(false);
                }
            }
        } else if (fsDisplay instanceof FS_ItemDisplay itemDisplay && data instanceof ItemHologramData itemData) {
            // item
            itemDisplay.setItem(itemData.getItemStack());
        } else if (fsDisplay instanceof FS_BlockDisplay blockDisplay && data instanceof BlockHologramData blockData) {
            // block

//            BlockType blockType = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK).get(blockData.getBlock().getKey());
            blockDisplay.setBlock(blockData.getBlock().createBlockData().createBlockState());
        }

        if (data instanceof DisplayHologramData displayData) {
            // billboard data
            fsDisplay.setBillboard(FS_Display.Billboard.valueOf(displayData.getBillboard().name()));

            // brightness
            if (displayData.getBrightness() != null) {
                fsDisplay.setBrightnessOverride(displayData.getBrightness().getBlockLight() << 4 | displayData.getBrightness().getSkyLight() << 20);
            }

            // entity transformation
            fsDisplay.setTranslation(displayData.getTranslation());
            fsDisplay.setScale(displayData.getScale());
            fsDisplay.setLeftRotation(new Quaternionf());
            fsDisplay.setRightRotation(new Quaternionf());

            // entity shadow
            fsDisplay.setShadowRadius(displayData.getShadowRadius());
            fsDisplay.setShadowStrength(displayData.getShadowStrength());

            fsDisplay.setViewRange(displayData.getVisibilityDistance());
        }
    }


    @Override
    public boolean show(@NotNull final Player player) {
        if (!new HologramShowEvent(this, player).callEvent()) {
            return false;
        }

        if (this.fsDisplay == null) {
            create(); // try to create it if it doesn't exist every time
        }

        if (fsDisplay == null) {
            return false; // could not be created, nothing to show
        }

        if (!data.getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
            return false;
        }

        // TODO: cache player protocol version
        // TODO: fix this
//        final var protocolVersion = FancyHologramsPlugin.get().isUsingViaVersion() ? Via.getAPI().getPlayerVersion(player) : MINIMUM_PROTOCOL_VERSION;
//        if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
//            return false;
//        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.add(player.getUniqueId());
        refreshHologram(player);

        return true;
    }

    @Override
    public boolean hide(@NotNull final Player player) {
        if (!new HologramHideEvent(this, player).callEvent()) {
            return false;
        }

        if (fsDisplay == null) {
            return false; // doesn't exist, nothing to hide
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.remove(player.getUniqueId());
        return true;
    }


    @Override
    public void refresh(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!isViewer(player)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                        fsDisplay.getId(),
                        data.getLocation().x(),
                        data.getLocation().y(),
                        data.getLocation().z(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch(),
                        true)
                .send(fsPlayer);


        if (fsDisplay instanceof FS_TextDisplay textDisplay) {
            textDisplay.setText(getShownText(player));
        }

        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsDisplay);
    }

}
