package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.Hologram;
import de.oliver.fancyholograms.utils.HologramSpigotAdapter;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.events.NpcModifyEvent;
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
            case LOCATION, TYPE -> {
                for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
                    if(hologram.getLinkedNpc() == null){
                        continue;
                    }

                    if(hologram.getLinkedNpc() != event.getNpc()){
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
                boolean isLinked = false;
                for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
                    if(hologram.getLinkedNpc() != null && hologram.getLinkedNpc().getName().equals(event.getNpc().getName())){
                        isLinked = true;
                        break;
                    }
                }
                if(isLinked){
                    event.setCancelled(true);
                    System.out.println("pog?");
                    MessageHelper.error(event.getPlayer(), "This modification is not allowed on a linked npc");
                }
            }
        }
    }

}
