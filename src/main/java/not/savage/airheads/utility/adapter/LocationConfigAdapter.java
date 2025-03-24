package not.savage.airheads.utility.adapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * A {@link TypeSerializer} for {@link Location} objects.
 */
public class LocationConfigAdapter implements TypeSerializer<Location> {

    private final String LOCATION_WORLD_NODE = "world";
    private final String LOCATION_X_POS_NODE = "x";
    private final String LOCATION_Y_POS_NODE = "y";
    private final String LOCATION_Z_POS_NODE = "z";
    private final String LOCATION_PITCH_NODE = "pitch";
    private final String LOCATION_YAW_NODE   = "yaw";

    @Override
    public Location deserialize(Type type, ConfigurationNode value) throws SerializationException {
        NamespacedKey worldKey = value.node(LOCATION_WORLD_NODE).get(NamespacedKey.class);

        if (worldKey == null) {
            throw new SerializationException("Invalid world key");
        }

        double x = getValueIfPresent(value, LOCATION_X_POS_NODE, Double.class, 0.0D);
        double y = getValueIfPresent(value, LOCATION_Y_POS_NODE, Double.class, 0.0D);
        double z = getValueIfPresent(value, LOCATION_Z_POS_NODE, Double.class, 0.0D);

        float pitch = getValueIfPresent(value, LOCATION_PITCH_NODE, Float.class, 0.0F);
        float yaw = getValueIfPresent(value, LOCATION_YAW_NODE, Float.class, 0.0F);

        return new Location(Bukkit.getWorld(worldKey), x, y, z, yaw, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node(LOCATION_WORLD_NODE).set(obj.getWorld().getKey());
        node.node(LOCATION_X_POS_NODE).set(obj.getX());
        node.node(LOCATION_Y_POS_NODE).set(obj.getY());
        node.node(LOCATION_Z_POS_NODE).set(obj.getZ());
        node.node(LOCATION_PITCH_NODE).set(obj.getPitch());
        node.node(LOCATION_YAW_NODE).set(obj.getYaw());
    }

    private <V> V getValueIfPresent(ConfigurationNode node, String field, Class<V> type, V defValue) throws SerializationException {
        if (!node.hasChild(field)) {
            return defValue;
        }
        return node.node(field).get(type);
    }
}