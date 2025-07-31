package not.savage.airheads.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.tasks.HologramUpdateTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// Replica of Protocol https://minecraft.wiki/w/Java_Edition_protocol/Entity_metadata#Text_Display
@Getter
public class TextDisplayHologram {

    // Entity Meta
    private final int entityId;
    private final UUID uuid;
    private final AirHeadsPlugin plugin;
    private final HologramConfig config;

    private final World world;

    // Display Entity
    private final int interpolationDelay;
    private final int transformationInterpolationDelay;
    private final int positionInterpolationDelay;

    private final Vector3f transformation;
    private final Vector3f scale;

    // Config doesn't modify these.
    private final Quaternion4f rotationLeft = new Quaternion4f(0, 0, 0, 1);
    private final Quaternion4f rotationRight = new Quaternion4f(0, 0, 0, 1);

    private byte billboardConstraints = 0; // 0 = FIXED, 1 = VERTICAL, 2 = HORIZONTAL, 3 = CENTERED
    // No Config for brightness override.
    private final int brightnessOverride = -1; // blockLight << 4 | skyLight << 20

    private final float viewRange;
    private final float shadowRadius;
    private final float shadowStrength;

    private final float width;
    private final float height;
    private final float pitch;
    private final float yaw;

    private int glowOverride = -1;

    // Text Display
    private final List<String> text;

    private int lineWidth = 200;

    private int backgroundColor = 0x4000000;
    // Config doesn't modify this.
    private final byte textOpacity = -1;
    @Setter private byte meta = 0;

    private double offset;
    private final HologramUpdateTask hologramUpdateTask;

    private transient Location currentLocation;

    public TextDisplayHologram(final AirHeadsPlugin plugin, final HologramConfig config,
                               final int entityId, final UUID uuid, final World world) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.config = config;
        this.plugin = plugin;
        this.world = world;

        this.interpolationDelay = config.getUpdateIntervalTicks() == -1 ? 0 : config.getUpdateIntervalTicks();
        this.positionInterpolationDelay = config.getUpdateIntervalTicks() == -1 ? 0 : config.getUpdateIntervalTicks();
        this.transformationInterpolationDelay = config.getUpdateIntervalTicks() == -1 ? 0 : config.getUpdateIntervalTicks();

        this.transformation = new Vector3f(config.getTranslationX(), config.getTranslationY(), config.getTranslationZ());
        this.scale = new Vector3f(config.getScaleX(), config.getScaleY(), config.getScaleZ());

        BillboardConstraints.fromString(config.getBillboardConstraints()).ifPresentOrElse(
                constraints -> this.billboardConstraints = constraints.getId(),
                () -> {
                    plugin.getLogger().warning("Unknown billboard constraint: " + config.getBillboardConstraints() + ", must be one of: " +
                            Arrays.toString(BillboardConstraints.values()));
                    this.billboardConstraints = BillboardConstraints.FIXED.getId();
                }
        );

        this.text = config.getHologramText();

        TextAlignment.fromString(config.getTextAlignment()).ifPresentOrElse(
                alignment -> alignment.align(this),
                () -> {
                    plugin.getLogger().warning("Unknown text alignment: " + config.getTextAlignment() + ", must be one of: " +
                            Arrays.toString(TextAlignment.values()));
                    TextAlignment.CENTERED.align(this);
                }
        );

        this.shadowRadius = config.getShadowRadius();
        this.shadowStrength = config.getShadowStrength();
        this.viewRange = config.getRenderDistance();

        this.width = config.getWidth();
        this.height = config.getHeight();
        this.pitch = config.getPitch();
        this.yaw = config.getYaw();

        this.setHasShadow(config.isHasTextShadow());
        this.setIsSeeThrough(config.isTransparentBackground());

        String hex = config.getBackgroundColor();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        this.backgroundColor = (int) Long.parseLong(hex, 16);
        setUseDefaultBackground(this.backgroundColor < 0 || this.backgroundColor == 0x40000000);

        this.offset = config.getHologramOffset();

        if (config.getUpdateIntervalTicks() != -1) {
            this.hologramUpdateTask = new HologramUpdateTask(this, config.getUpdateIntervalTicks());
            this.hologramUpdateTask.register();
        } else {
            this.hologramUpdateTask = null;
        }
    }

    public WrapperPlayServerEntityMetadata toPacket(final Player player) {
        return new WrapperPlayServerEntityMetadata(entityId, packDirty(player));
    }

    private List<EntityData<?>> packDirty(final Player player) {
        final List<EntityData<?>> data = new ArrayList<>();

        // DisplayEntity Fields
        data.add(new EntityData<>(8, EntityDataTypes.INT, interpolationDelay));
        data.add(new EntityData<>(9, EntityDataTypes.INT, transformationInterpolationDelay));
        data.add(new EntityData<>(10, EntityDataTypes.INT, positionInterpolationDelay));
        data.add(new EntityData<>(11, EntityDataTypes.VECTOR3F, transformation));
        data.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, scale));
        data.add(new EntityData<>(13, EntityDataTypes.QUATERNION, rotationLeft));
        data.add(new EntityData<>(14, EntityDataTypes.QUATERNION, rotationRight));
        data.add(new EntityData<>(15, EntityDataTypes.BYTE, billboardConstraints));
        data.add(new EntityData<>(16, EntityDataTypes.INT, brightnessOverride));
        data.add(new EntityData<>(17, EntityDataTypes.FLOAT, viewRange));
        data.add(new EntityData<>(18, EntityDataTypes.FLOAT, shadowRadius));
        data.add(new EntityData<>(19, EntityDataTypes.FLOAT, shadowStrength));
        data.add(new EntityData<>(20, EntityDataTypes.FLOAT, width));
        data.add(new EntityData<>(21, EntityDataTypes.FLOAT, height));
        data.add(new EntityData<>(22, EntityDataTypes.INT, glowOverride));

        // TextDisplay Fields
        final List<String> playerView = new ArrayList<>(PlaceholderAPI.setPlaceholders(player, text));
        data.add(
                new EntityData<>(
                        23,
                        EntityDataTypes.ADV_COMPONENT,
                        MiniMessage.miniMessage().deserialize(String.join("\n", playerView))
                )
        );
        data.add(new EntityData<>(24, EntityDataTypes.INT, lineWidth));
        data.add(new EntityData<>(25, EntityDataTypes.INT, backgroundColor));
        data.add(new EntityData<>(26, EntityDataTypes.BYTE, textOpacity));
        data.add(new EntityData<>(27, EntityDataTypes.BYTE, meta));

        return data;
    }

    public void spawn(final Player player, final Location location) {
        location.add(0, getOffset(), 0);
        location.setPitch(pitch);
        location.setYaw(pitch);
        this.currentLocation = location;
        CompletableFuture.runAsync(() -> {
            // Packet to spawn the actual text-display entity.
            final WrapperPlayServerSpawnEntity addDisplayEntityPacket = new WrapperPlayServerSpawnEntity(
                    getEntityId(),
                    getUuid(),
                    SpigotConversionUtil.fromBukkitEntityType(EntityType.TEXT_DISPLAY),
                    SpigotConversionUtil.fromBukkitLocation(location),
                    0.0F,
                    0,
                    null
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, addDisplayEntityPacket); // Send entity spawn
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, toPacket(player)); // Send update entity
        });
    }

    public void update(final Player player) {
        CompletableFuture.runAsync(() -> {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, toPacket(player));
        });
    }

    public void teleport(final Location location) {
        if (!location.getWorld().getName().equals(getWorld().getName()))
            throw new IllegalStateException("Cannot teleport to a different world!");
        location.add(0, getOffset(), 0);
        location.setPitch(pitch);
        location.setYaw(yaw);
        this.currentLocation = location;
        CompletableFuture.runAsync(() -> {
            final WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport(
                    getEntityId(),
                    SpigotConversionUtil.fromBukkitLocation(location),
                    false
            );
            location.getWorld().getPlayers().forEach(player -> PacketEvents.getAPI().getPlayerManager().sendPacket(player, teleportPacket));
        });
    }

    public void setHasShadow(boolean hasShadow) {
        if (hasShadow) {
            meta |= 0x01;
        } else {
            meta &= ~0x01;
        }
    }

    public void setIsSeeThrough(boolean isSeeThrough) {
        if (isSeeThrough) {
            meta |= 0x02;
        } else {
            meta &= ~0x02;
        }
    }

    public void setUseDefaultBackground(boolean useDefaultBackground) {
        if (useDefaultBackground) {
            meta |= 0x04;
        } else {
            meta &= ~0x04;
        }
    }
}
