package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerGroup;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public abstract class MultiLocationMarkerGroup extends MarkerGroup<MultiLocationMarkerGroup> {
    protected final HashMap<Long, List<Marker>> chunkMarkersMap = new HashMap<>();
    protected final HashMap<Long, List<BlockDisplay>> blockDisplays = new HashMap<>();
    protected final List<Marker> markers;
    private final World world;

    public MultiLocationMarkerGroup(MarkerManager markerManager, List<Marker> markers, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, onAdd, onRemove);
        this.markers = markers;
        this.world = markers.getFirst().location.getWorld();
        for (Marker marker : markers) {
            if (!world.equals(marker.location.getWorld())) throw new IllegalArgumentException("Markers are not in the same world!");
            long key = Chunk.getChunkKey(marker.location);
            chunkMarkersMap.putIfAbsent(key, new ArrayList<>());
            chunkMarkersMap.get(key).add(marker);
            if (marker.location.isChunkLoaded()) {
                blockDisplays.putIfAbsent(key, new ArrayList<>());
                blockDisplays.get(key).add(marker.spawn());
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public final void load(Chunk chunk) {
        if (!chunk.getWorld().equals(world)) return;
        if (blockDisplays.containsKey(chunk.getChunkKey())) return;
        if (!chunkMarkersMap.containsKey(chunk.getChunkKey())) return;
        var displays = chunkMarkersMap.get(chunk.getChunkKey()).stream().map(Marker::spawn).toList();
        blockDisplays.put(chunk.getChunkKey(), displays);
        for (BlockDisplay display : displays) {
            makeVisible(display);
            onAdd.accept(display);
        }
    }

    @ApiStatus.Internal
    @Override
    public final void unload(Chunk chunk) {
        if (!chunk.getWorld().equals(world)) return;
        if (!blockDisplays.containsKey(chunk.getChunkKey())) return;
        var displays = blockDisplays.remove(chunk.getChunkKey());
        for (BlockDisplay display : displays) {
            onRemove.accept(display);
            display.remove();
        }
    }


    @Override
    public void remove() {
        super.remove();
        for (List<BlockDisplay> displays : blockDisplays.values()) {
            for (BlockDisplay display : displays) {
                onRemove.accept(display);
                display.remove();
            }
        }
        blockDisplays.clear();
    }
}
