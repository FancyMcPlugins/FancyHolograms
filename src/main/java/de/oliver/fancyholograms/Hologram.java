package de.oliver.fancyholograms;

import com.mojang.math.Transformation;
import de.oliver.fancyholograms.events.HologramSpawnEvent;
import de.oliver.fancynpcs.Npc;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.adventure.PaperAdventure;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Hologram {

    private final String name;
    private Location location;
    private List<String> lines;
    private Display.BillboardConstraints billboard;
    private float scale;
    private ChatFormatting background;
    private float shadowRadius;
    private float shadowStrength;
    private int updateTextInterval; // if < 0 = no update
    private long lastTextUpdate; // millisecond timestamp
    private Npc linkedNpc;

    private Display.TextDisplay entity;
    private boolean isDirty;
    private final Map<UUID, Boolean> isVisibleForPlayer = new HashMap<>();

    public Hologram(String name, Location location, List<String> lines, Display.BillboardConstraints billboard, float scale, ChatFormatting background, float shadowRadius, float shadowStrength, int updateTextInterval, Npc linkedNpc) {
        this.name = name;
        this.location = location;
        this.lines = lines;
        this.billboard = billboard;
        this.scale = scale;
        this.background = background;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.updateTextInterval = updateTextInterval;
        this.linkedNpc = linkedNpc;
        this.lastTextUpdate = System.currentTimeMillis();
        this.isDirty = false;
    }

    public void create(){
        Level level = ((CraftWorld) location.getWorld()).getHandle();
        entity = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        entity.setLineWidth(1000);

        FancyHolograms.getInstance().getHologramManager().addHologram(this);
    }

    public void delete(){
        FancyHolograms.getInstance().getHologramManager().removeHologram(this);
        entity = null;
    }

    public void spawn(ServerPlayer serverPlayer){
        if(!location.getWorld().getName().equalsIgnoreCase(serverPlayer.getLevel().getWorld().getName())){
            return;
        }

        HologramSpawnEvent hologramSpawnEvent = new HologramSpawnEvent(this, serverPlayer.getBukkitEntity());
        hologramSpawnEvent.callEvent();

        if(hologramSpawnEvent.isCancelled()){
            return;
        }

        if(entity == null){
            create();
        }

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entity);
        serverPlayer.connection.send(addEntityPacket);

        syncWithNpc();
        updateLocation(serverPlayer);
        updateText(serverPlayer);
        updateBillboard(serverPlayer);
        updateScale(serverPlayer);
        updateBackground(serverPlayer);
        updateShadow(serverPlayer);
        syncWithNpc();
        isVisibleForPlayer.put(serverPlayer.getUUID(), true);
    }

    public void remove(ServerPlayer serverPlayer) {
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(entity.getId());
        serverPlayer.connection.send(removeEntitiesPacket);
        isVisibleForPlayer.put(serverPlayer.getUUID(), false);
    }

    public void updateText(ServerPlayer serverPlayer){
        if(serverPlayer != null) {
            entity.setText(getText(serverPlayer.getBukkitEntity()));
            entity.getEntityData().refresh(serverPlayer);
        } else {
            entity.setText(getText(null));
        }
    }

    public void updateLocation(ServerPlayer serverPlayer){
        entity.level = ((CraftWorld) location.getWorld()).getHandle();
        entity.setPosRaw(location.x(), location.y(), location.z());
        entity.setYRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(entity);
        serverPlayer.connection.send(teleportEntityPacket);
    }

    public void updateBillboard(ServerPlayer serverPlayer){
        entity.setBillboardConstraints(billboard);


        if(serverPlayer != null) {
            entity.getEntityData().refresh(serverPlayer);
        }
    }

    public void updateScale(ServerPlayer serverPlayer){
        Transformation transformation = new Transformation(
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        );
        entity.setTransformation(transformation);


        if(serverPlayer != null) {
            entity.getEntityData().refresh(serverPlayer);
        }
    }

    public void updateBackground(ServerPlayer serverPlayer){
        if(background == ChatFormatting.RESET || background == null){
            entity.setBackgroundColor(Display.TextDisplay.INITIAL_BACKGROUND);
        } else if(background == ChatFormatting.ITALIC){ // transparent
            entity.setBackgroundColor(0);
        } else {
            entity.setBackgroundColor(background.getColor() | 0xC8000000);
        }


        if(serverPlayer != null) {
            entity.getEntityData().refresh(serverPlayer);
        }
    }

    public void updateShadow(ServerPlayer serverPlayer) {
        entity.setShadowRadius(shadowRadius);
        entity.setShadowStrength(shadowStrength);

        if (serverPlayer != null) {
            entity.getEntityData().refresh(serverPlayer);
        }
    }

    public void syncWithNpc(){
        if(linkedNpc == null)
            return;

        linkedNpc.updateDisplayName("<empty>");
        linkedNpc.updateShowInTab(false);

        location = linkedNpc.getLocation().clone().add(0, 2.1, 0);
        isDirty = true;
    }

    private Component getText(Player player){
        String t = String.join("\n", lines);
        TagResolver resolver = TagResolver.empty();

        if (player != null) {
            if (FancyHolograms.getInstance().isUsingPlaceholderApi()) {
                t = PlaceholderAPI.setPlaceholders(player, t);
            }
            if (FancyHolograms.getInstance().isUsingMiniplaceholders()) {
                resolver = MiniPlaceholders.getAudienceGlobalPlaceholders(player);
            }
        }

        return PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(t, resolver));
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.isDirty = true;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        this.isDirty = true;
    }

    public Display.BillboardConstraints getBillboard() {
        return billboard;
    }

    public void setBillboard(Display.BillboardConstraints billboard) {
        this.billboard = billboard;
        this.isDirty = true;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.isDirty = true;
    }

    public ChatFormatting getBackground() {
        return background;
    }

    public void setBackground(ChatFormatting background) {
        this.background = background;
        this.isDirty = true;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public void setShadowStrength(float shadowStrength) {
        this.shadowStrength = shadowStrength;
    }

    public int getUpdateTextInterval() {
        return updateTextInterval;
    }

    public void setUpdateTextInterval(int updateTextInterval) {
        this.updateTextInterval = updateTextInterval;
    }

    public long getLastTextUpdate() {
        return lastTextUpdate;
    }

    public void setLastTextUpdate(long lastTextUpdate) {
        this.lastTextUpdate = lastTextUpdate;
    }

    public Npc getLinkedNpc() {
        return linkedNpc;
    }

    public void setLinkedNpc(Npc linkedNpc) {
        this.linkedNpc = linkedNpc;
    }

    public Display.TextDisplay getEntity() {
        return entity;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public Map<UUID, Boolean> getIsVisibleForPlayer() {
        return isVisibleForPlayer;
    }
}
