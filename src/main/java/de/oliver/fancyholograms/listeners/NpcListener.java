package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class NpcListener implements Listener {

    private final @NotNull FancyHologramsPlugin plugin;

    public NpcListener(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRemove(@NotNull final NpcRemoveEvent event) {
        this.plugin.getRegistry()
                .getAll()
                .stream()
                .filter(hologram -> event.getNpc().getData().getName().equals(hologram.getData().getLinkedNpcName()))
                .forEach(hologram -> hologram.getData().setLinkedNpcName(null));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onModify(@NotNull final NpcModifyEvent event) {
        final var holograms = this.plugin.getRegistry().getAll();

        switch (event.getModification()) {
            case TYPE, LOCATION, SCALE -> {
                final var needsToBeUpdated = holograms.stream()
                        .filter(hologram -> event.getNpc().getData().getName().equals(hologram.getData().getLinkedNpcName()))
                        .toList();

                FancyLib.getInstance().getScheduler().runTaskLater(null, 1L, () -> needsToBeUpdated.forEach(this.plugin.getControllerImpl()::syncHologramWithNpc));
            }
            case DISPLAY_NAME, SHOW_IN_TAB -> {
                final var isLinked = holograms.stream()
                        .map(Hologram::getData)
                        .map(HologramData::getLinkedNpcName)
                        .anyMatch(event.getNpc().getData().getName()::equals);

                if (isLinked) {
                    event.setCancelled(true);
                    MessageHelper.error(event.getModifier(), "This modification is not allowed on a hologram linked npc");
                }
            }
        }
    }

}
