package not.savage.airheads.utility;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import not.savage.airheads.utility.adapter.LocationConfigAdapter;
import not.savage.airheads.utility.adapter.NamespacedKeyAdapter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A builder for creating and saving configuration files using Configurate.
 * This is a copy-paste class I, @Savagelife, have on hand for creating configuration files.
 * So some methods are unused in this project & could be removed. /shrug.
 * @see org.spongepowered.configurate.objectmapping.ConfigSerializable
 * @param <T> The class of the configuration file.
 */
public class ConfigBuilder<T> {

    @Getter @Setter private Path path;
    private final Class<T> clazz;
    private final YamlConfigurationLoader.Builder builder;
    private final ConfigurationOptions options = ConfigurationOptions.defaults();
    private final TypeSerializerCollection.Builder typeSerializers = TypeSerializerCollection.defaults().childBuilder();

    public ConfigBuilder(@NonNull Class<T> clazz) {
        if (!clazz.isAnnotationPresent(ConfigSerializable.class)) {
            throw new IllegalArgumentException("Class must be annotated with @ConfigSerializable");
        }
        this.clazz = clazz;
        this.builder = YamlConfigurationLoader.builder()
                .indent(2)
                .nodeStyle(
                        NodeStyle.BLOCK
                );

        typeSerializers.register(Location.class, new LocationConfigAdapter()).register(NamespacedKey.class, new NamespacedKeyAdapter());
    }

    @Contract("_->this")
    public ConfigBuilder<T> withPath(Path path) {
        this.path = path;
        builder.source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

        return this;
    }

    @Contract("_->this")
    public ConfigBuilder<T> forFile(@NonNull File f) {
        withPath(f.toPath());
        return this;
    }

    @Contract("_,_->this")
    public <Y> ConfigBuilder<T> addAdaptor(Class<Y> clazz, TypeSerializer<Y> serializer) {
        this.typeSerializers.register(clazz, serializer);
        return this;
    }

    public void save(T config) {
        try {
            YamlConfigurationLoader loader = builder.defaultOptions(options.serializers(typeSerializers.build())).build();
            CommentedConfigurationNode node = loader.load();
            node.set(clazz, config);
            loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Contract("->!null")
    public T build() {
        ConfigurationOptions finalOptions = options.serializers(typeSerializers.build());
        builder.defaultOptions(finalOptions);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                try {
                    T instance = (T) clazz.getDeclaredConstructors()[0].newInstance();

                    YamlConfigurationLoader loader = builder.build();
                    CommentedConfigurationNode node = loader.load();
                    node.set(clazz, instance);
                    loader.save(node);

                    return instance;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            YamlConfigurationLoader loader = builder.build();
            CommentedConfigurationNode node = loader.load();
            T t = node.get(clazz);
            loader.save(node);
            return t;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
