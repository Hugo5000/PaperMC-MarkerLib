package at.hugob.plugin.library.marker;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class MarkerGroup<T extends MarkerGroup<T>> {
    private AtomicBoolean removed = new AtomicBoolean(false);
    protected final MarkerManager markerManager;

    protected final Consumer<BlockDisplay> onAdd;
    protected final Consumer<BlockDisplay> onRemove;

    private final HashSet<UUID> players = new HashSet<>();

    public MarkerGroup(MarkerManager markerManager, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        this.markerManager = markerManager;
        this.onAdd = onAdd;
        this.onRemove = onRemove;
    }

    @ApiStatus.Internal
    public abstract void load(Chunk chunk);
    @ApiStatus.Internal
    public abstract void unload(Chunk chunk);

    public T addViewer(Player player) {
        players.add(player.getUniqueId());
        //noinspection unchecked
        return (T) this;
    }

    public T removeViewer(Player player) {
        players.remove(player.getUniqueId());
        //noinspection unchecked
        return (T) this;
    }

    public final void remove() {
        if (!removed.getAndSet(true)) {
            markerManager.remove(this);
            getRemoval().run();
        }
    }

    protected final void makeVisible(BlockDisplay display) {
        for (UUID uuid : players) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.showEntity(markerManager.plugin, display);
            }
        }
    }

    @ApiStatus.Internal
    public abstract Runnable getRemoval();
}
