package not.savage.airheads.utility.adapter;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SoundAdapter implements TypeSerializer<Sound> {
    @Override
    public Sound deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(node.get(NamespacedKey.class));
    }

    @Override
    public void serialize(Type type, @Nullable Sound obj, ConfigurationNode node) throws SerializationException {
        node.set(NamespacedKey.class, new NamespacedKey(obj.key().namespace(), obj.key().value()));
    }
}