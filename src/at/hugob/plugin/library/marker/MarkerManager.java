package at.hugob.plugin.library.marker;

import at.hugob.plugin.library.marker.shapes.CoordinateSystemMarker;
import at.hugob.plugin.library.marker.shapes.DotMarker;
import at.hugob.plugin.library.marker.shapes.RegionMarker;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class MarkerManager {
    private final static Consumer<BlockDisplay> NOOP = b -> {
    };
    public final JavaPlugin plugin;

    private final Set<MarkerGroup<?>> markerGroups = Collections.newSetFromMap(new WeakHashMap<>());

    public MarkerManager(final JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new MarkerListener(this), plugin);
    }

    public CoordinateSystemMarker createCoordinateSystemAt(Location location) {
        var coordinateSystem = new CoordinateSystemMarker(this, location, NOOP, NOOP);
        markerGroups.add(coordinateSystem);
        return coordinateSystem;
    }

    public DotMarker createDotAt(Location location, float size, BlockData blockData, Color color) {
        DotMarker dotMarker = new DotMarker(this, location, size, blockData, color, NOOP, NOOP);
        markerGroups.add(dotMarker);
        return dotMarker;
    }

    public RegionMarker createRegion(Location min, Location max, BlockData blockData, Color color) {
        var marker = new RegionMarker(this, min, max, 5, blockData, color, NOOP, NOOP);
        markerGroups.add(marker);
        return marker;
    }

    void remove(MarkerGroup<?> markerGroup) {
        this.markerGroups.remove(markerGroup);
    }

    @ApiStatus.Internal
    public void loadChunk(Chunk chunk) {
        for (MarkerGroup<?> markerGroup : markerGroups) {
            markerGroup.load(chunk);
        }
    }

    @ApiStatus.Internal
    public void unloadChunk(Chunk chunk) {
        for (MarkerGroup<?> markerGroup : markerGroups) {
            markerGroup.unload(chunk);
        }
    }
}
