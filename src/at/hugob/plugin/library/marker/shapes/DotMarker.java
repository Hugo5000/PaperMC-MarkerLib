package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class DotMarker extends SingleLocationMarkerGroup<DotMarker> {
    public DotMarker(MarkerManager markerManager, Location location, float size, BlockData blockData, @Nullable Color color, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, List.of(
            new Marker(blockData.clone(), location, new Vector3f(-size / 2, -size / 2, -size / 2), new Quaternionf(), new Vector3f(size, size, size), color)
        ), location, onAdd, onRemove);
    }

    @Override
    public DotMarker scale(Vector3f scale) {
        var marker = markers.getFirst();
        marker.scale = scale;
        marker.translation = marker.scale.div(-2)
            .rotate(marker.rotation.invert());
        if (blockDisplays.isEmpty()) return this;
        var blockDisplay = blockDisplays.getFirst();
        var t = blockDisplay.getTransformation();
        blockDisplay.setTransformation(new Transformation(
            marker.translation,
            t.getLeftRotation(),
            marker.scale,
            t.getRightRotation()
        ));
        return this;
    }

    @Override
    public DotMarker rotation(Quaternionf rotation) {
        var marker = markers.getFirst();
        marker.rotation = rotation;
        marker.translation = marker.scale.div(-2)
            .rotate(marker.rotation.invert());
        if (blockDisplays.isEmpty()) return this;
        var blockDisplay = blockDisplays.getFirst();
        var t = blockDisplay.getTransformation();
        blockDisplay.setTransformation(new Transformation(
            marker.translation,
            rotation,
            marker.scale,
            t.getRightRotation()
        ));
        return this;
    }
}
