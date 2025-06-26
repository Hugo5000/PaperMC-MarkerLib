package at.hugob.plugin.library.marker;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class MarkerGroup<T extends MarkerGroup<T>> {
    protected final MarkerManager markerManager;

    protected final Consumer<BlockDisplay> onAdd;
    protected final Consumer<BlockDisplay> onRemove;

    private final HashSet<UUID> players = new HashSet<>();

    public MarkerGroup(MarkerManager markerManager, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        this.markerManager = markerManager;
        this.onAdd = onAdd;
        this.onRemove = onRemove;
    }

    public abstract void load(Chunk chunk);

    public abstract void unload(Chunk chunk);

    @SuppressWarnings("unchecked")
    public T addViewer(Player player) {
        players.add(player.getUniqueId());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T removeViewer(Player player) {
        players.remove(player.getUniqueId());
        return (T) this;
    }

    public void remove() {
        markerManager.remove(this);
    }

    protected final void makeVisible(BlockDisplay display) {
        for (UUID uuid : players) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.showEntity(markerManager.plugin, display);
            }
        }
    }
}
