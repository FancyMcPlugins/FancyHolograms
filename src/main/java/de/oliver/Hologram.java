package de.oliver;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.List;

public class Hologram {

    private final String name;
    private Location location;
    private List<String> lines;
    private Display.BillboardConstraints billboard;
    private Display.TextDisplay entity;

    public Hologram(String name, Location location, List<String> lines, Display.BillboardConstraints billboard) {
        this.name = name;
        this.location = location;
        this.lines = lines;
        this.billboard = billboard;
    }

    public void create(){
        Level level = ((CraftWorld) location.getWorld()).getHandle();
        entity = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        entity.setPosRaw(location.x(), location.y(), location.z());
        entity.setYRot(location.getYaw());
        entity.setBillboardConstraints(billboard);

        FancyHolograms.getInstance().getHologramManager().addHologram(this);
    }

    public void delete(){
        FancyHolograms.getInstance().getHologramManager().removeHologram(this);
        entity = null;
    }

    public void spawn(ServerPlayer serverPlayer){
        if(entity == null){
            create();
        }

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entity);
        serverPlayer.connection.send(addEntityPacket);

        updateText(serverPlayer);
    }

    public void remove(ServerPlayer serverPlayer) {
        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(entity.getId());
        serverPlayer.connection.send(removeEntitiesPacket);
    }

    public void updateText(ServerPlayer serverPlayer){
        entity.setText(getText());

        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty());
        serverPlayer.connection.send(setEntityDataPacket);
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

        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty());
        serverPlayer.connection.send(setEntityDataPacket);
    }

    private Component getText(){
        String t = String.join("\n", lines);
        return PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(t));
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public Display.BillboardConstraints getBillboard() {
        return billboard;
    }

    public void setBillboard(Display.BillboardConstraints billboard) {
        this.billboard = billboard;
    }

    public Display.TextDisplay getEntity() {
        return entity;
    }
}
