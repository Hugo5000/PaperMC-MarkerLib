package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerGroup;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SingleLocationMarkerGroup<T extends SingleLocationMarkerGroup<T>> extends MarkerGroup<T> {
    private final Location location;

    protected final List<Marker> markers;
    protected final List<BlockDisplay> blockDisplays;

    public SingleLocationMarkerGroup(MarkerManager markerManager, List<Marker> markers, Location location, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, onAdd, onRemove);
        this.markers = markers;
        this.location = location;
        this.blockDisplays = new ArrayList<>(markers.size());
        if (location.isChunkLoaded()) load(location.getChunk());
    }

    @ApiStatus.Internal
    @Override
    public final void load(Chunk chunk) {
        if (!blockDisplays.isEmpty()) return;
        if (location.getBlockX() >> 4 == chunk.getX()
            && location.getBlockZ() >> 4 == chunk.getZ()
            && location.getWorld().equals(chunk.getWorld())) {
            markers.stream().map(Marker::spawn).forEach(blockDisplay -> {
                blockDisplays.add(blockDisplay);
                makeVisible(blockDisplay);
                onAdd.accept(blockDisplay);
            });
        }
    }

    @ApiStatus.Internal
    @Override
    public final void unload(Chunk chunk) {
        if (blockDisplays.isEmpty()) return;
        if (location.getBlockX() >> 4 == chunk.getX()
            && location.getBlockZ() >> 4 == chunk.getZ()
            && location.getWorld().equals(chunk.getWorld())) {
            for (BlockDisplay display : blockDisplays) {
                onRemove.accept(display);
                display.remove();
            }
            blockDisplays.clear();
        }
    }

    @Override
    public Runnable getRemoval() {
        return internalRemoval(markerManager, blockDisplays, onRemove);
    }
    private static Runnable internalRemoval(MarkerManager manager, List<BlockDisplay> blockDisplays, Consumer<BlockDisplay> onRemove) {
        return () -> {
            Bukkit.getScheduler().runTask(manager.plugin, () -> {
                for (BlockDisplay display : blockDisplays) {
                    onRemove.accept(display);
                    display.remove();
                }
                blockDisplays.clear();
            });
        };
    }

    @SuppressWarnings("unchecked")
    public T teleport(Location location) {
        markers.forEach(marker -> marker.location = location);
        if (blockDisplays.isEmpty()) return (T) this;
        blockDisplays.forEach(blockDisplay -> blockDisplay.teleportAsync(location));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T rotation(Quaternionf rotation) {
        markers.forEach(marker -> marker.rotation = rotation);
        for (BlockDisplay blockDisplay : blockDisplays) {
            var t = blockDisplay.getTransformation();
            blockDisplay.setTransformation(new Transformation(
                t.getTranslation().rotate(t.getLeftRotation().invert()).rotate(rotation),
                rotation,
                t.getScale(),
                t.getRightRotation()
            ));
        }
        return (T) this;
    }

    public T scale(float scale) {
        return scale(new Vector3f(scale, scale, scale));
    }

    public abstract T scale(Vector3f scale);

    @Override
    public T addViewer(Player player) {
        super.addViewer(player);
        for (BlockDisplay display : blockDisplays) {
            player.showEntity(markerManager.plugin, display);
        }
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public T removeViewer(Player player) {
        super.removeViewer(player);
        for (BlockDisplay display : blockDisplays) {
            player.hideEntity(markerManager.plugin, display);
        }
        //noinspection unchecked
        return (T) this;
    }
}
