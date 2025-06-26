package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RegionMarker extends MultiLocationMarkerGroup {

    public RegionMarker(MarkerManager markerManager,
                        Location min, Location max, double maxLength,
                        BlockData blockData, @Nullable Color color,
                        Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, createMarkers(min, max, maxLength, blockData, color), onAdd, onRemove);
    }

    private static List<Marker> createMarkers(Location min, Location max, double maxLength, BlockData blockData, @Nullable Color color) {
        if (!min.getWorld().equals(max.getWorld())) throw new IllegalArgumentException("Locations must be in the same world!");
        var actualMin = new Location(min.getWorld(),
            Math.min(min.x(), max.x()),
            Math.min(min.y(), max.y()),
            Math.min(min.z(), max.z())
        );
        var actualMax = new Location(min.getWorld(),
            Math.max(min.x(), max.x()) + 1,
            Math.max(min.y(), max.y()) + 1,
            Math.max(min.z(), max.z()) + 1
        );
        var markers = new ArrayList<Marker>();

        double sideLength = actualMax.x() - actualMin.x();
        double stepSize = sideLength / Math.ceil(sideLength / maxLength);

        for (double x = actualMin.x() + stepSize; x < actualMax.x(); x += stepSize) {
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), x, actualMin.y(), actualMin.z()),
                new Vector3f((float) -stepSize / 2, 0, 0),
                new Quaternionf(),
                new Vector3f((float) stepSize, 0.02f, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), x, actualMax.y(), actualMax.z()),
                new Vector3f((float) -stepSize / 2, 0, 0),
                new Quaternionf(),
                new Vector3f((float) stepSize, 0.02f, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), x, actualMax.y(), actualMin.z()),
                new Vector3f((float) -stepSize / 2, 0, 0),
                new Quaternionf(),
                new Vector3f((float) stepSize, 0.02f, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), x, actualMin.y(), actualMax.z()),
                new Vector3f((float) -stepSize / 2, 0, 0),
                new Quaternionf(),
                new Vector3f((float) stepSize, 0.02f, 0.02f),
                color
            ));
        }

        sideLength = actualMax.y() - actualMin.y();
        stepSize = sideLength / Math.ceil(sideLength / maxLength);
        for (double y = actualMin.y() + stepSize; y < actualMax.y(); y += stepSize) {
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMin.x(), y, actualMin.z()),
                new Vector3f(0, (float) -stepSize / 2, 0),
                new Quaternionf(),
                new Vector3f(0.02f, (float) stepSize, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMax.x(), y, actualMax.z()),
                new Vector3f(0, (float) -stepSize / 2, 0),
                new Quaternionf(),
                new Vector3f(0.02f, (float) stepSize, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMax.x(), y, actualMin.z()),
                new Vector3f(0, (float) -stepSize / 2, 0),
                new Quaternionf(),
                new Vector3f(0.02f, (float) stepSize, 0.02f),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMin.x(), y, actualMax.z()),
                new Vector3f(0, (float) -stepSize / 2, 0),
                new Quaternionf(),
                new Vector3f(0.02f, (float) stepSize, 0.02f),
                color
            ));
        }

        sideLength = actualMax.z() - actualMin.z();
        stepSize = sideLength / Math.ceil(sideLength / maxLength);
        for (double z = actualMin.y() + stepSize; z < actualMax.z(); z += stepSize) {
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMin.x(), actualMin.y(), z),
                new Vector3f(0, 0, (float) -stepSize / 2),
                new Quaternionf(),
                new Vector3f(0.02f, 0.02f, (float) stepSize),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMax.x(), actualMax.y(), z),
                new Vector3f(0, 0, (float) -stepSize / 2),
                new Quaternionf(),
                new Vector3f(0.02f, 0.02f, (float) stepSize),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMax.x(), actualMin.y(), z),
                new Vector3f(0, 0, (float) -stepSize / 2),
                new Quaternionf(),
                new Vector3f(0.02f, 0.02f, (float) stepSize),
                color
            ));
            markers.add(new Marker(blockData, new Location(actualMin.getWorld(), actualMin.x(), actualMax.y(), z),
                new Vector3f(0, 0, (float) -stepSize / 2),
                new Quaternionf(),
                new Vector3f(0.02f, 0.02f, (float) stepSize),
                color
            ));
        }

        return markers;
    }
}
