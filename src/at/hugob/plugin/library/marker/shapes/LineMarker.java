package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class LineMarker extends MultiLocationMarkerGroup {
    private static final Vector3f NORMAL_DIRECTION = new Vector3f(1, 0, 0);

    public LineMarker(MarkerManager markerManager, Collection<Location> locations, float segmentLength, float lineWidth, float lineHeight, BlockData blockData, @Nullable Color color, @Nullable BlockData nodeData, @Nullable Color nodeColor, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, createLine(locations, segmentLength, lineWidth, lineHeight, blockData, color, nodeData, nodeColor), onAdd, onRemove);
    }

    private static List<Marker> createLine(Collection<Location> locations, float segmentLength,
                                           float lineWidth, float lineHeight,
                                           BlockData lineBlockData, @Nullable Color lineColor,
                                           @Nullable BlockData nodeBlockData, @Nullable Color nodeColor) {
        Location previous = null;
        List<Marker> markers = new ArrayList<>();
        if (nodeBlockData == null) {
            nodeBlockData = lineBlockData;
        }
        if (nodeColor == null) {
            nodeColor = lineColor;
        }

        var nodeScale = new Vector3f(lineWidth, lineHeight, lineWidth).add(0.02f, 0.02f, 0.02f);
        var nodeOffset = new Vector3f(nodeScale).div(-2);

        for (Location location : locations) {
            location = location.setRotation(0, 0);
            if (previous != null) {
                var segment = location.toVector().subtract(previous.toVector());
                var nodeLineOffset = segment.toVector3f().setComponent(1, 0);
                if (nodeLineOffset.lengthSquared() > 0) {
                    nodeLineOffset = nodeLineOffset.normalize().mul(lineWidth / 2 - lineHeight / 2);
                }
                segment.subtract(Vector.fromJOML(nodeLineOffset)).subtract(Vector.fromJOML(nodeLineOffset));
                var rotation = new Quaternionf()
                    .rotateTo(segment.toVector3f().setComponent(1, 0), segment.toVector3f())
                    .rotateTo(NORMAL_DIRECTION, segment.toVector3f().setComponent(1, 0));
                var length = segment.length();
                var segmentCount = (int) Math.ceil(length / segmentLength);
                var actualLength = (float) length / segmentCount;
                var scale = new Vector3f(actualLength, lineHeight, lineWidth);
                var locationSteps = Vector.fromJOML(rotation.transform(new Vector3f(actualLength, 0, 0)));
                var loc = previous.clone().add(locationSteps.clone().multiply(0.5)).add(Vector.fromJOML(nodeLineOffset));
                var translation = new Vector3f(scale).div(-2);
                for (int i = 0; i < segmentCount; i++) {
                    markers.add(new Marker(lineBlockData, loc, new Vector3f(translation).rotate(rotation), rotation, scale, lineColor));
                    loc.add(locationSteps);
                }
            }
            markers.add(new Marker(nodeBlockData, location, nodeOffset, new Quaternionf(), nodeScale, nodeColor));
            previous = location;
        }


        return markers;
    }
}
