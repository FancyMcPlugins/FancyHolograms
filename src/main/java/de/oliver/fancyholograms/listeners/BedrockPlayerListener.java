package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.events.HologramShowEvent;
import de.oliver.fancyholograms.config.FHFeatureFlags;
import de.oliver.fancyholograms.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.geysermc.floodgate.api.FloodgateApi;

public class BedrockPlayerListener implements Listener {

    @EventHandler
    public void onHologramShow(final HologramShowEvent event) {
        if (FHFeatureFlags.DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS.isEnabled() && PluginUtils.isFloodgateEnabled()) {
            boolean isBedrockPlayer = FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
            if (isBedrockPlayer) {
                event.setCancelled(true);
            }
        }
    }

}
