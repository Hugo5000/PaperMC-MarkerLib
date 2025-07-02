package at.hugob.plugin.library.marker;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Marker {
    /**
     * The namespaced key to identify marker entities
     */
    public static final NamespacedKey PDC_KEY = new NamespacedKey("markerlib", "is_marker_entity");

    public @Nullable Color glowColor;

    public BlockData blockData;

    public Location location;

    public Vector3f translation;
    public Quaternionf rotation;
    public Vector3f scale;

    public Marker(BlockData blockData, Location location, Vector3f translation, Quaternionf rotation, Vector3f scale, @Nullable Color glowColor) {
        this.blockData = blockData.clone();
        this.location = location.clone();
        this.translation = new Vector3f(translation);
        this.rotation = new Quaternionf(rotation);
        this.scale = new Vector3f(scale);
        this.glowColor = glowColor;
    }

    public BlockDisplay spawn() {
        return location.getWorld().spawn(location, BlockDisplay.class, display -> {
            display.setBlock(blockData);
            display.setTransformation(new Transformation(
                translation,
                rotation,
                scale,
                new Quaternionf()
            ));
            display.setPersistent(false);
            display.setVisibleByDefault(false);
            display.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BOOLEAN, true);
            if (glowColor != null) {
                display.setGlowing(true);
                display.setGlowColorOverride(glowColor);
            }
        });
    }

    /**
     * An helper method to identify Marker entities
     *
     * @param entity the entity to check
     * @return true if it s a marker false otherwise
     */
    public static boolean isMarker(final @Nullable Entity entity) {
        return entity instanceof BlockDisplay // implicit null check
            && entity.getPersistentDataContainer().has(PDC_KEY, PersistentDataType.BOOLEAN);
    }
}
