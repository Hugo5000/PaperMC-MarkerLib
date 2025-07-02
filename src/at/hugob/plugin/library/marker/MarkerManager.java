package at.hugob.plugin.library.marker;

import at.hugob.plugin.library.marker.shapes.CoordinateSystemMarker;
import at.hugob.plugin.library.marker.shapes.DotMarker;
import at.hugob.plugin.library.marker.shapes.LineMarker;
import at.hugob.plugin.library.marker.shapes.RegionMarker;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class MarkerManager {
    private final static Consumer<BlockDisplay> NOOP = b -> {
    };
    private static final Cleaner cleaner = Cleaner.create();
    public final JavaPlugin plugin;

    private final Set<MarkerGroup<?>> markerGroups = Collections.newSetFromMap(new WeakHashMap<>());

    public MarkerManager(final JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new MarkerListener(this), plugin);
    }

    public CoordinateSystemMarker createCoordinateSystemAt(Location location) {
        var coordinateSystem = new CoordinateSystemMarker(this, location, NOOP, NOOP);
        add(coordinateSystem);
        return coordinateSystem;
    }

    public DotMarker createDotAt(Location location, float size, BlockData blockData, @Nullable Color color) {
        DotMarker dotMarker = new DotMarker(this, location, size, blockData, color, NOOP, NOOP);
        add(dotMarker);
        return dotMarker;
    }

    public RegionMarker createRegion(Location min, Location max, float maxSegmentLength, float thickness, BlockData blockData, @Nullable Color color) {
        var marker = new RegionMarker(this, min, max, maxSegmentLength, thickness, blockData, color, NOOP, NOOP);
        add(marker);
        return marker;
    }

    public LineMarker createLine(Collection<Location> locations, float maxSegmentLength, float lineWidth, float lineHeight, BlockData lineBlockData, @Nullable Color lineColor, BlockData nodeBlockData, @Nullable Color nodeColor) {
        var marker = new LineMarker(this, locations, maxSegmentLength, lineWidth, lineHeight, lineBlockData, lineColor, nodeBlockData, nodeColor, NOOP, NOOP);
        add(marker);
        return marker;
    }

    private void add(MarkerGroup<?> group) {
        markerGroups.add(group);
        cleaner.register(group, group.getRemoval());
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
