package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.events.NpcModifyEvent;
import de.oliver.fancynpcs.events.NpcRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class NpcListener implements Listener {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public NpcListener(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onRemove(@NotNull final NpcRemoveEvent event) {
        this.plugin.getHologramsManager()
                   .getHolograms()
                   .stream()
                   .filter(hologram -> event.getNpc().getName().equals(hologram.getData().getLinkedNpcName()))
                   .forEach(hologram -> hologram.getData().setLinkedNpcName(null));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onModify(@NotNull final NpcModifyEvent event) {
        final var holograms = this.plugin.getHologramsManager().getHolograms();

        switch (event.getModification()) {
            case TYPE, LOCATION -> {
                final var needsToBeUpdated = holograms.stream()
                                                      .filter(hologram -> event.getNpc().getName().equals(hologram.getData().getLinkedNpcName()))
                                                      .toList();

                this.plugin.getScheduler()
                           .runTaskLater(null, 1L, () -> {
                               final var players = Bukkit.getOnlinePlayers();

                               needsToBeUpdated.forEach(this.plugin.getHologramsManager()::syncHologramWithNpc);
                               needsToBeUpdated.forEach(hologram -> hologram.refreshHologram(players));
                           });
            }
            case DISPLAY_NAME, SHOW_IN_TAB -> {
                final var isLinked = holograms.stream()
                                              .map(Hologram::getData)
                                              .map(HologramData::getLinkedNpcName)
                                              .anyMatch(event.getNpc().getName()::equals);

                if (isLinked) {
                    event.setCancelled(true);
                    MessageHelper.error(event.getPlayer(), "This modification is not allowed on a hologram linked npc");
                }
            }
        }
    }

}
