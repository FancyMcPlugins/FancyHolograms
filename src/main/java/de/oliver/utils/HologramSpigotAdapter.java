package de.oliver.utils;

import de.oliver.Hologram;
import net.minecraft.ChatFormatting;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class HologramSpigotAdapter extends Hologram {

    public HologramSpigotAdapter(Hologram hologram){
        super(
                hologram.getName(),
                hologram.getLocation(),
                hologram.getLines(),
                hologram.getBillboard(),
                hologram.getScale(),
                hologram.getBackground(),
                hologram.getUpdateTextInterval()
        );
    }

    public void spawn(Player serverPlayer) {
        super.spawn(((CraftPlayer)serverPlayer).getHandle());
    }

    public void remove(Player player) {
        super.remove(((CraftPlayer) player).getHandle());
    }

    public void updateText(Player player) {
        super.updateText(((CraftPlayer) player).getHandle());
    }

    public void updateLocation(Player player) {
        super.updateLocation(((CraftPlayer) player).getHandle());
    }

    public void updateBillboard(Player player) {
        super.updateBillboard(((CraftPlayer) player).getHandle());
    }

    public void updateScale(Player player) {
        super.updateScale(((CraftPlayer) player).getHandle());
    }

    public void updateBackground(Player player) {
        super.updateBackground(((CraftPlayer) player).getHandle());
    }

    public void setBillboard(Display.Billboard billboard) {
        super.setBillboard(net.minecraft.world.entity.Display.BillboardConstraints.valueOf(billboard.name()));
    }

    public void setBackground(ChatColor background) {
        super.setBackground(ChatFormatting.getByCode(background.getChar()));
    }

    public static HologramSpigotAdapter fromHologram(Hologram hologram){
        return new HologramSpigotAdapter(hologram);
    }
}
