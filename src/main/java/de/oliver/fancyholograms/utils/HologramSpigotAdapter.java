package de.oliver.fancyholograms.utils;

import de.oliver.fancyholograms.Hologram;
import net.minecraft.ChatFormatting;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class HologramSpigotAdapter {

    private final Hologram hologram;
    public HologramSpigotAdapter(Hologram hologram) {
        this.hologram = hologram;
    }

    public void spawn(Player serverPlayer) {
        hologram.spawn(((CraftPlayer)serverPlayer).getHandle());
    }

    public void remove(Player player) {
        hologram.remove(((CraftPlayer) player).getHandle());
    }

    public void updateText(Player player) {
        hologram.updateText(((CraftPlayer) player).getHandle());
    }

    public void updateLocation(Player player) {
        hologram.updateLocation(((CraftPlayer) player).getHandle());
    }

    public void updateBillboard(Player player) {
        hologram.updateBillboard(((CraftPlayer) player).getHandle());
    }

    public void updateScale(Player player) {
        hologram.updateScale(((CraftPlayer) player).getHandle());
    }

    public void updateBackground(Player player) {
        hologram.updateBackground(((CraftPlayer) player).getHandle());
    }

    public void updateShadow(Player player){
        hologram.updateShadow(((CraftPlayer) player).getHandle());
    }

    public void updateTextShadow(Player player){
        hologram.updateTextShadow(((CraftPlayer) player).getHandle());
    }

    public void setBillboard(Display.Billboard billboard) {
        hologram.setBillboard(net.minecraft.world.entity.Display.BillboardConstraints.valueOf(billboard.name()));
    }

    public void setBackground(ChatColor background) {
        hologram.setBackground(ChatFormatting.getByCode(background.getChar()));
    }

    public static HologramSpigotAdapter fromHologram(Hologram hologram){
        return new HologramSpigotAdapter(hologram);
    }
}
