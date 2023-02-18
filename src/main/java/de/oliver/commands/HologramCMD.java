package de.oliver.commands;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HologramCMD implements CommandExecutor, TabExecutor {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
        }

        CraftPlayer craftPlayer = (CraftPlayer) p;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        ArmorStand textDisplay = new ArmorStand(serverPlayer.getLevel(), serverPlayer.position().x(), serverPlayer.position().y() - 2f, serverPlayer.position().z());


        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(textDisplay);
        serverPlayer.connection.send(addEntityPacket);

        textDisplay.setCustomNameVisible(true);
        textDisplay.setCustomName(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize("<rainbow>test 123 123123</rainbow>")));
        textDisplay.setInvisible(true);

        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(textDisplay.getId(), textDisplay.getEntityData().packDirty());
        serverPlayer.connection.send(setEntityDataPacket);

        return false;
    }
}