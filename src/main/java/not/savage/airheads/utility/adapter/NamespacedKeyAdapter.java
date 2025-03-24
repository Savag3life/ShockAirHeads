package not.savage.airheads.utility.adapter;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * A {@link TypeSerializer} for {@link NamespacedKey} objects.
 */
public class NamespacedKeyAdapter implements TypeSerializer<NamespacedKey> {

    @Override
    public NamespacedKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String namespaceKeyPair = node.getString();
        if (namespaceKeyPair == null) {
            throw new SerializationException("NamespacedKey must be a string");
        }

        String[] split = namespaceKeyPair.split(":");
        if (split.length < 2) {
            throw new SerializationException("NamespacedKey must be in the format of 'namespace:key'");
        }

        return new NamespacedKey(split[0], split[1]);
    }

    @Override
    public void serialize(Type type, @Nullable NamespacedKey obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new SerializationException("NamespacedKey cannot be null");
        }
        node.set(obj.toString());
    }
}
