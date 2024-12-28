package de.oliver.fancyholograms.hologram.version;

import com.viaversion.viaversion.api.Via;
import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.events.HologramDespawnEvent;
import de.oliver.fancyholograms.api.events.HologramSpawnEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.config.FHFeatureFlags;
import de.oliver.fancyholograms.util.PluginUtils;
import de.oliver.fancysitula.api.entities.*;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public final class HologramImpl extends Hologram {

    private FS_Display fsDisplay;

    public HologramImpl(@NotNull final HologramData data) {
        super(data);

        final var location = data.getLocation();
        if (!location.isWorldLoaded()) {
            return;
        }

        switch (data.getType()) {
            case TEXT -> this.fsDisplay = new FS_TextDisplay();
            case ITEM -> this.fsDisplay = new FS_ItemDisplay();
            case BLOCK -> this.fsDisplay = new FS_BlockDisplay();
        }
    }


    @Override
    public void spawnTo(@NotNull final Player player) {
        if (!new HologramSpawnEvent(this, player).callEvent()) {
            return;
        }

        if (fsDisplay == null) {
            return; // could not be created, nothing to show
        }

        if (!data.getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
            return;
        }

        if (FHFeatureFlags.DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS.isEnabled()) {
            final var protocolVersion = PluginUtils.isViaVersionEnabled() ? Via.getAPI().getPlayerVersion(player.getUniqueId()) : MINIMUM_PROTOCOL_VERSION;
            if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
                FancyHolograms.get().getFancyLogger().debug("Player " + player.getName() + " is using an outdated protocol version (" + protocolVersion + "). Hologram will not be shown.");
                return;
            }
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.add(player.getUniqueId());
        updateFor(player);

        traitTrait.onSpawn(player);
    }

    @Override
    public void despawnFrom(@NotNull final Player player) {
        if (!new HologramDespawnEvent(this, player).callEvent()) {
            return;
        }

        if (fsDisplay == null) {
            return; // doesn't exist, nothing to hide
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.remove(player.getUniqueId());

        traitTrait.onDespawn(player);
    }


    @Override
    public void updateFor(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to refresh
        }

        syncWithData();

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

    private void syncWithData() {
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
            // interpolation
            fsDisplay.setTransformationInterpolationDuration(displayData.getInterpolationDuration());
            fsDisplay.setTransformationInterpolationStartDeltaTicks(0);

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

}
