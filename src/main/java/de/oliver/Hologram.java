package de.oliver;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

import java.util.List;

public class Hologram {

    private final String name;
    private Location location;
    private List<String> lines;
    private ArmorStand entity;

    public Hologram(String name, Location location, List<String> lines) {
        this.name = name;
        this.location = location;
        this.lines = lines;
    }

    public void create(){
        Level level = ((CraftWorld) location.getWorld()).getHandle();
        entity = new ArmorStand(level, location.x(), location.y() - 2f, location.z());

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
        entity.setCustomNameVisible(true);
        entity.setCustomName(getText());
        entity.setInvisible(true);

        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty());
        serverPlayer.connection.send(setEntityDataPacket);
    }

    public void updateLocation(ServerPlayer serverPlayer){
        entity.level = ((CraftWorld) location.getWorld()).getHandle();
        entity.setPosRaw(location.x(), location.y(), location.z());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(entity);
        serverPlayer.connection.send(teleportEntityPacket);
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

    public ArmorStand getEntity() {
        return entity;
    }
}
