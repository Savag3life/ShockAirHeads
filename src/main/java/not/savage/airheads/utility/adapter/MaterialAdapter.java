package not.savage.airheads.utility.adapter;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class MaterialAdapter implements TypeSerializer<Material> {

    @Override
    public Material deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            String value = node.getString();
            if (value == null) {
                throw new SerializationException("Material name cannot be null");
            }
            return Material.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new SerializationException("Invalid Material name: " + node.getString());
        }
    }

    @Override
    public void serialize(Type type, @Nullable Material obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new SerializationException("NamespacedKey cannot be null");
        }
        node.set(obj.name());
    }
}
