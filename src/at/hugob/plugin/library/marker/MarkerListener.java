package at.hugob.plugin.library.marker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

class MarkerListener implements Listener {
    private final MarkerManager manager;

    MarkerListener(final MarkerManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnloadChunk(final ChunkUnloadEvent event) {
        manager.unloadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoadChunk(final ChunkLoadEvent event) {
        manager.loadChunk(event.getChunk());
    }
}
