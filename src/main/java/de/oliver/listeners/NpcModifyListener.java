package de.oliver.listeners;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import de.oliver.events.NpcModifyEvent;
import de.oliver.utils.HologramSpigotAdapter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcModifyListener implements Listener {

    @EventHandler
    public void onNpcModify(NpcModifyEvent event){
        if(!FancyHolograms.getInstance().isUsingFancyNpcs()){
            return;
        }


        switch (event.getModification()){
            case LOCATION -> {
                for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
                    if(hologram.getLinkedNpc() == null){
                        continue;
                    }

                    FancyHolograms.getInstance().getLogger().info("Syncing npc");

                    Bukkit.getScheduler().runTaskLater(FancyHolograms.getInstance(), () -> {
                        hologram.syncWithNpc();
                        HologramSpigotAdapter adapter = HologramSpigotAdapter.fromHologram(hologram);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            adapter.updateLocation(player);
                        }
                    }, 1);

                }
            }

            case DISPLAY_NAME, SHOW_IN_TAB -> {
                event.setCancelled(true);
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>This modification is not allowed on a linked npc</red>"));
            }
        }
    }

}
