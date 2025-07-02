package at.hugob.plugin.library.marker.shapes;

import at.hugob.plugin.library.marker.Marker;
import at.hugob.plugin.library.marker.MarkerManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class CoordinateSystemMarker extends SingleLocationMarkerGroup<CoordinateSystemMarker> {
    public CoordinateSystemMarker(MarkerManager markerManager, Location location, Consumer<BlockDisplay> onAdd, Consumer<BlockDisplay> onRemove) {
        super(markerManager, List.of(
            new Marker(BlockType.RED_CONCRETE.createBlockData(), location, new Vector3f(-.01f, -.01f, -.01f), new Quaternionf(), new Vector3f(1, .02f, .02f), Color.RED),
            new Marker(BlockType.BLUE_CONCRETE.createBlockData(), location, new Vector3f(-.01f, -.01f, -.01f), new Quaternionf(), new Vector3f(.02f, 1, .02f), Color.BLUE),
            new Marker(BlockType.GREEN_CONCRETE.createBlockData(), location, new Vector3f(-.01f, -.01f, -.01f), new Quaternionf(), new Vector3f(.02f, .02f, 1), Color.GREEN),
            new Marker(BlockType.WHITE_CONCRETE.createBlockData(), location, new Vector3f(-.02f, -.02f, -.02f), new Quaternionf(), new Vector3f(.04f, .04f, 0.04f), Color.WHITE)
        ), location, onAdd, onRemove);
    }

    @Override
    public CoordinateSystemMarker scale(Vector3f scale) {
        scale = new Vector3f(scale).div(2);
        for (int i = 0; i < 3; i++) {
            var marker = markers.get(i);
            float scaleVal = Math.max(0.1f, Math.abs(scale.get(i))) * Math.signum(scale.get(i));
            if(scaleVal == 0) scaleVal = 0.1f;
            marker.scale.setComponent(i, scaleVal);
        }
        if (!blockDisplays.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                var blockDisplay = blockDisplays.get(i);
                var t = blockDisplay.getTransformation();
                float scaleVal = Math.max(0.1f, Math.abs(scale.get(i))) * Math.signum(scale.get(i));
                if(scaleVal == 0) scaleVal = 0.1f;
                blockDisplay.setTransformation(new Transformation(
                    t.getTranslation(),
                    t.getLeftRotation(),
                    t.getScale().setComponent(i, scaleVal),
                    t.getRightRotation()
                ));
            }
        }
        return this;
    }
}
