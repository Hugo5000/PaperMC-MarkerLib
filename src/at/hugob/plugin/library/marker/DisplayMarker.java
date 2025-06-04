package at.hugob.plugin.library.marker;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Creates simple Display Block Markers, from Axi, A dot or an Area Cube
 */
public class DisplayMarker {

    private DisplayMarker() {}

    /**
     * The namespaced key to identify marker entities
     */
    public static NamespacedKey MARKER_KEY = new NamespacedKey("glows", "is_glow_entity");

    /**
     * An helper method to identify Marker entities
     *
     * @param entity the entitiy to check
     * @return true if it s a marker false otherwise
     */
    public static boolean isMarker(Entity entity) {
        return entity instanceof BlockDisplay && entity.getPersistentDataContainer().has(MARKER_KEY, PersistentDataType.BOOLEAN);
    }

    /**
     * Creates a Coordinate System around the location with the specific scale
     *
     * @param plugin   The plugin which makes this marker
     * @param player   The player who should be able to see this marker
     * @param location The location of this marker
     * @param xSize    The X axis scale
     * @param ySize    The Y axis scale
     * @param zSize    The Z axis scale
     * @return A Collection of Block entities used for this coordinate system
     */
    public static List<BlockDisplay> createCoordinateSystemAt(JavaPlugin plugin, Player player, Location location, float xSize, float ySize, float zSize) {
        ArrayList<BlockDisplay> entities = new ArrayList<>(1);
        createBlockDisplay(plugin, player, location, new Vector3f(0, 0, 0), new Vector3f(xSize, .02f, .02f), BlockType.RED_CONCRETE.createBlockData(), Color.RED, entities);
        createBlockDisplay(plugin, player, location, new Vector3f(0, 0, 0), new Vector3f(.02f, ySize, .02f), BlockType.BLUE_CONCRETE.createBlockData(), Color.BLUE, entities);
        createBlockDisplay(plugin, player, location, new Vector3f(0, 0, 0), new Vector3f(.02f, .02f, zSize), BlockType.GREEN_CONCRETE.createBlockData(), Color.GREEN, entities);
        return entities;
    }

    /**
     * Creates a single glowing Block Display at the wanted position and scale
     *
     * @param plugin   The plugin which makes this marker
     * @param player   The player who should be able to see this marker
     * @param location The location of this marker
     * @param xSize    The X axis scale
     * @param ySize    The Y axis scale
     * @param zSize    The Z axis scale
     * @return A Collection of Block entities used for this dot marker
     */
    public static List<BlockDisplay> createDotAt(JavaPlugin plugin, Player player, Location location, float xSize, float ySize, float zSize) {
        return createDotAt(plugin, player, location, xSize, ySize, zSize, BlockType.WHITE_CONCRETE.createBlockData(), Color.WHITE);
    }

    /**
     * Creates a single glowing Block Display at the wanted position and scale
     *
     * @param plugin    The plugin which makes this marker
     * @param player    The player who should be able to see this marker
     * @param location  The location of this marker
     * @param xSize     The X axis scale
     * @param ySize     The Y axis scale
     * @param zSize     The Z axis scale
     * @param blockData The BlockData the blockdisplay should have
     * @param color     The Color the block display should glow with
     * @return A Collection of Block entities used for this dot marker
     */
    public static List<BlockDisplay> createDotAt(JavaPlugin plugin, Player player, Location location, float xSize, float ySize, float zSize, BlockData blockData, Color color) {
        ArrayList<BlockDisplay> entities = new ArrayList<>(1);
        createBlockDisplay(plugin, player, location, new Vector3f(0, 0, 0), new Vector3f(xSize, ySize, zSize), blockData, color, entities);
        return entities;
    }

    /**
     * Creates a cube surrounding the specified area
     *
     * @param plugin The plugin which makes this marker
     * @param player The player who should be able to see this marker
     * @param pos1   The first corner of the Area
     * @param pos2   The second corner of the Area
     * @return A Collection of Block entities used for this area marker
     */
    public static List<BlockDisplay> createAreaMarkerAt(JavaPlugin plugin, Player player, Location pos1, Location pos2) {
        return createAreaMarkerAt(plugin, player, pos1, pos2, BlockType.WHITE_CONCRETE.createBlockData(), Color.WHITE);
    }

    /**
     * Creates a cube surrounding the specified area
     *
     * @param plugin    The plugin which makes this marker
     * @param player    The player who should be able to see this marker
     * @param pos1      The first corner of the Area
     * @param pos2      The second corner of the Area
     * @param blockData The BlockData the blockdisplay should have
     * @param color     The Color the block display should glow with
     * @return A Collection of Block entities used for this area marker
     */
    public static List<BlockDisplay> createAreaMarkerAt(JavaPlugin plugin, Player player, Location pos1, Location pos2, BlockData blockData, Color color) {
        if (pos1.getWorld() != pos2.getWorld()) return Collections.EMPTY_LIST;
        float dx = (float) Math.abs(pos1.getX() - pos2.getX()) + 1; // + 1 to include the outer corner of the area
        float dy = (float) Math.abs(pos1.getY() - pos2.getY()) + 1; // + 1 to include the outer corner of the area
        float dz = (float) Math.abs(pos1.getZ() - pos2.getZ()) + 1; // + 1 to include the outer corner of the area
        var loc = new Location(pos1.getWorld(),
            Math.min(pos1.getX(), pos2.getX()) + dx / 2,
            Math.min(pos1.getY(), pos2.getY()) + dx / 2,
            Math.min(pos1.getZ(), pos2.getZ()) + dx / 2
        );
        return createCuboidAt(plugin, player, loc, dx, dy, dz, blockData, color);
    }

    /**
     * Creates a cube surrounding the specified area
     *
     * @param plugin   The plugin which makes this marker
     * @param player   The player who should be able to see this marker
     * @param location The Center of the Cuboid
     * @param xSize    The X axis size
     * @param ySize    The Y axis size
     * @param zSize    The Z axis size
     * @return A Collection of Block entities used for this cuboid marker
     */
    public static List<BlockDisplay> createCuboidAt(JavaPlugin plugin, Player player, Location location, float xSize, float ySize, float zSize) {
        return createCuboidAt(plugin, player, location, xSize, ySize, zSize, BlockType.WHITE_CONCRETE.createBlockData(), Color.WHITE);
    }

    /**
     * Creates a cube surrounding the specified area
     *
     * @param plugin    The plugin which makes this marker
     * @param player    The player who should be able to see this marker
     * @param location  The Center of the Cuboid
     * @param xSize     The X axis size
     * @param ySize     The Y axis size
     * @param zSize     The Z axis size
     * @param blockData The BlockData the blockdisplay should have
     * @param color     The Color the block display should glow with
     * @return A Collection of Block entities used for this cuboid marker
     */
    public static List<BlockDisplay> createCuboidAt(JavaPlugin plugin, Player player, Location location, float xSize, float ySize, float zSize, BlockData blockData, Color color) {
        location = location.clone().setRotation(0, 0);
        ArrayList<BlockDisplay> entities = new ArrayList<>(12);
        for (var translation : List.of(
            new Vector3f(-xSize / 2f, -ySize / 2f, -zSize / 2f),
            new Vector3f(-xSize / 2f, -ySize / 2f, zSize / 2f),
            new Vector3f(-xSize / 2f, ySize / 2f, -zSize / 2f),
            new Vector3f(-xSize / 2f, ySize / 2f, zSize / 2f)
        )) {
            createBlockDisplay(plugin, player, location, translation, new Vector3f(xSize, .02f, .02f), blockData, color, entities);
        }
        for (var translation : List.of(
            new Vector3f(-xSize / 2f, -ySize / 2f, -zSize / 2f),
            new Vector3f(-xSize / 2f, -ySize / 2f, zSize / 2f),
            new Vector3f(xSize / 2f, -ySize / 2f, -zSize / 2f),
            new Vector3f(xSize / 2f, -ySize / 2f, zSize / 2f)
        )) {
            createBlockDisplay(plugin, player, location, translation, new Vector3f(.02f, ySize, .02f), blockData, color, entities);
        }
        for (var translation : List.of(
            new Vector3f(-xSize / 2f, -ySize / 2f, -zSize / 2f),
            new Vector3f(-xSize / 2f, ySize / 2f, -zSize / 2f),
            new Vector3f(xSize / 2f, -ySize / 2f, -zSize / 2f),
            new Vector3f(xSize / 2f, ySize / 2f, -zSize / 2f)
        )) {
            createBlockDisplay(plugin, player, location, translation, new Vector3f(0.02f, .02f, zSize), blockData, color, entities);
        }
        entities.stream().skip(1).forEach(entities.get(0)::addPassenger);
        return entities;
    }

    /**
     * Rotates the markers to look into the desired direction
     *
     * @param entities Markers to rotate
     * @param pitch    pitch in degrees
     * @param yaw      yaw in degrees
     */
    public static void rotateTo(Collection<BlockDisplay> entities, float pitch, float yaw) {
        float yawRad = (float) Math.toRadians(-yaw);     // negative due to coordinate system
        float pitchRad = (float) Math.toRadians(pitch); // pitch also inverted

        Quaternionf q = new Quaternionf();

        q.rotateY(yawRad).rotateX(pitchRad);
        rotateTo(entities, q);
    }

    /**
     * Rotates all markers to face in the desired Quaternion direction
     *
     * @param entities  Markers to rotate
     * @param direction Direction to face
     */
    public static void rotateTo(Collection<BlockDisplay> entities, Quaternionf direction) {
        for (BlockDisplay display : entities) {
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(2);
            var trans = display.getTransformation();
            display.setTransformation(new Transformation(
                trans.getTranslation().rotate(trans.getLeftRotation().invert()).rotate(direction),
                direction,
                trans.getScale(),
                trans.getRightRotation()
            ));
        }
    }

    private static void createBlockDisplay(JavaPlugin plugin, Player player, Location location, Vector3f translation, Vector3f scale, BlockData blockData, Color color, ArrayList<BlockDisplay> entities) {
        player.getWorld().spawn(location, BlockDisplay.class, display -> {
            display.setBlock(blockData);
            display.setTransformation(new Transformation(
                translation,
                new Quaternionf(),
                scale,
                new Quaternionf()
            ));
            display.setPersistent(false);
            display.setGlowing(true);
            display.setVisibleByDefault(false);
            display.getPersistentDataContainer().set(MARKER_KEY, PersistentDataType.BOOLEAN, true);
            display.setGlowColorOverride(color);
            player.showEntity(plugin, display);
            entities.add(display);
        });
    }
}
