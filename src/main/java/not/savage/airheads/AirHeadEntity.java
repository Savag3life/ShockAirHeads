package not.savage.airheads;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import gg.optimalgames.hologrambridge.HologramAPI;
import gg.optimalgames.hologrambridge.hologram.Hologram;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.config.AirHead;
import not.savage.airheads.tasks.FloatAnimationTask;
import not.savage.airheads.utility.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class AirHeadEntity {

    // Armor Stand AABB is 0.5 x 0.5 x 1.975
    private final float DEFAULT_ARMOR_STAND_HEIGHT = 1.975F;

    private final AirHeadsPlugin plugin;
    private final AirHead config;

    private final int entityId;
    private final String name;
    private final UUID uuid;

    @Setter private Location currentLocation;
    @Getter private final ItemStack headItem;
    private final int floatTask;
    private final Hologram hologram;
    private final long activeAfter;

    /**
     * This plugin does not currently provide API support for
     * using AirHeads outside the configs of the commands & the config.
     * @param config The AirHead configuration
     * @param delayedTicks The number of ticks to delay the AirHead from spawning.
     */
    public AirHeadEntity(AirHeadsPlugin plugin, String name, AirHead config, long delayedTicks) {
        this.plugin = plugin;
        this.config = config;
        this.currentLocation = config.getLocation().clone().add(0.5, -trueHeight(), 0.5);
        this.activeAfter = System.currentTimeMillis() + (delayedTicks * 50);

        this.entityId = SpigotReflectionUtil.generateEntityId();
        this.name = name;
        this.uuid = UUID.randomUUID();

        this.floatTask = new FloatAnimationTask(currentLocation, this, activeAfter)
                .runTaskTimerAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), 0, 1)
                .getTaskId();

        if (!config.getHologramText().isEmpty()) {
            this.hologram = HologramAPI.createHologram(currentLocation.clone().add(0, getHologramOffset(), 0));
            for (String line : config.getHologramText()) {
                this.hologram.appendTextLine(MiniMessage.miniMessage().deserialize(line));
            }
            this.hologram.getVisibilityManager().setVisibleByDefault(false);
        } else {
            this.hologram = null;
        }

        final org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta meta = (SkullMeta) head.getItemMeta();
        Heads.setBase64ToSkullMeta(config.getHeadTexture(), meta);
        head.setItemMeta(meta);
        this.headItem = SpigotConversionUtil.fromBukkitItemStack(head);
    }

    public void spawnForPlayer(Player player) {
        // Spawn the entity.
        CompletableFuture.runAsync(() -> {
            final WrapperPlayServerSpawnEntity entitySpawnPacket = new WrapperPlayServerSpawnEntity(
                    this.entityId,
                    this.uuid,
                    SpigotConversionUtil.fromBukkitEntityType(EntityType.ARMOR_STAND),
                    SpigotConversionUtil.fromBukkitLocation(this.currentLocation),
                    0.0F,
                    0,
                    null
            );

            // Dress the entity
            final WrapperPlayServerEntityEquipment entityEquipPacket = new WrapperPlayServerEntityEquipment(
                    this.entityId,
                    List.of(new Equipment(
                            com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET,
                            headItem
                    ))
            );

            // Protect the entity
            final WrapperPlayServerEntityMetadata entityMetaPacket = new WrapperPlayServerEntityMetadata(
                    this.entityId,
                    List.of(
                            new EntityData( // Invisible
                                    0,
                                    EntityDataTypes.BYTE,
                                    (byte) 0x20
                            )
                    )
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entitySpawnPacket);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityEquipPacket);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMetaPacket);

            if (config.getScale() != 1.0) {
                // We only need to send an Attribute packet if the scale is not 1.0 (None-Default)
                WrapperPlayServerUpdateAttributes entityAttributes = createAttributePacket();
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityAttributes);
            }

        }).thenAcceptAsync(v -> {
            // May be needed to await completion and then execute after, but im
            // not sure of the behavior when entities are modified before the client knows they exist.
            // May be something to address later in a PR
        });
        this.hologram.getVisibilityManager().showTo(player);
    }

    private @NotNull WrapperPlayServerUpdateAttributes createAttributePacket() {
        final WrapperPlayServerUpdateAttributes.Property scale = new WrapperPlayServerUpdateAttributes.Property(
                Attributes.SCALE,
                config.getScale(),
                new ArrayList<>()
        );

        final WrapperPlayServerUpdateAttributes.Property interactionRange = new WrapperPlayServerUpdateAttributes.Property(
                Attributes.ENTITY_INTERACTION_RANGE,
                config.getScale(),
                new ArrayList<>()
        );

        return new WrapperPlayServerUpdateAttributes(getEntityId(), List.of(scale, interactionRange));
    }

    public void teleport(Location location) {
        final WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport(getEntityId(), SpigotConversionUtil.fromBukkitLocation(location), false);
        CompletableFuture.runAsync(() -> {
            getCurrentLocation().getWorld().getPlayers().forEach(player -> PacketEvents.getAPI().getPlayerManager().sendPacket(player, teleportPacket));
        });
        setCurrentLocation(location);

        if (getHologram() != null)
            getHologram().teleport(location.clone().add(0, getHologramOffset(), 0));
    }

    /**
     * Remove all the AirHead components from the world.
     */
    public void remove() {
        if (floatTask != -1) {
            Bukkit.getScheduler().cancelTask(floatTask);
        }

        final WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(getEntityId());
        currentLocation.getWorld().getPlayers().forEach(player -> {
            if (player.isOnline()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
            }
        });

        if (hologram != null) {
            hologram.delete();
        }
    }

    public double trueHeight() {
        if (config.getScale() == 1.0) return DEFAULT_ARMOR_STAND_HEIGHT;
        if (config.getScale() < 1.0) {
            return DEFAULT_ARMOR_STAND_HEIGHT * config.getScale();
        }
        return DEFAULT_ARMOR_STAND_HEIGHT + config.getScale();
    }

    public double getHologramOffset() {
        return trueHeight() + config.getHologramOffset();
    }
}
