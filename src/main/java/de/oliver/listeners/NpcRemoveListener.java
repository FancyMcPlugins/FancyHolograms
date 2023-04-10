package de.oliver.listeners;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import de.oliver.Npc;
import de.oliver.events.NpcRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcRemoveListener implements Listener {

    @EventHandler
    public void onNpcRemove(NpcRemoveEvent event){
        Npc npc = event.getNpc();

        for (Hologram hologram : FancyHolograms.getInstance().getHologramManager().getAllHolograms()) {
            if(hologram.getLinkedNpc() != null && hologram.getLinkedNpc() == npc){
                hologram.setLinkedNpc(null);
            }
        }
    }

}
