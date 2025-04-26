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
    private final UUID entityUuid;
    private final int glassEntityId;
    private final UUID glassEntityUuid;
    private final String name;

    @Setter private Location currentLocation;
    private final ItemStack headItem;
    private final ItemStack glassItem;
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
        this.entityUuid = UUID.randomUUID();
        this.glassEntityId = SpigotReflectionUtil.generateEntityId();
        this.glassEntityUuid = UUID.randomUUID();
        this.name = name;

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

        if (!config.getHeadTexture().equals("%player%")) {
            final org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
            final SkullMeta meta = (SkullMeta) head.getItemMeta();
            Heads.setBase64ToSkullMeta(config.getHeadTexture(), meta);
            head.setItemMeta(meta);
            this.headItem = SpigotConversionUtil.fromBukkitItemStack(head);
        } else {
            this.headItem = null;
        }

        if (!config.getOverlayMaterial().isAir()) {
            this.glassItem = SpigotConversionUtil.fromBukkitItemStack(new org.bukkit.inventory.ItemStack(config.getOverlayMaterial(), 1));
        } else {
            this.glassItem = null;
        }
    }

    public void spawnForPlayer(Player player) {
        // Spawn the entity.
        CompletableFuture.runAsync(() -> {
            final WrapperPlayServerSpawnEntity entitySpawnPacket = new WrapperPlayServerSpawnEntity(
                    entityId,
                    entityUuid,
                    SpigotConversionUtil.fromBukkitEntityType(EntityType.ARMOR_STAND),
                    SpigotConversionUtil.fromBukkitLocation(currentLocation),
                    0.0F,
                    0,
                    null
            );

            WrapperPlayServerEntityEquipment entityEquipPacket
            if (headItem == null) {
                // Reflection of players skins.
                final org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
                final SkullMeta meta = (SkullMeta) head.getItemMeta();
                Heads.setReflectionSkin(player, meta);
                head.setItemMeta(meta);
                // Dress the entity
                entityEquipPacket = new WrapperPlayServerEntityEquipment(
                        entityId,
                        List.of(new Equipment(
                                com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET,
                                SpigotConversionUtil.fromBukkitItemStack(head)
                        ))
                );
            } else {
                // Dress the entity
                entityEquipPacket = new WrapperPlayServerEntityEquipment(
                        entityId,
                        List.of(new Equipment(
                                com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET,
                                headItem
                        ))
                );
            }

            // Protect the entity
            final WrapperPlayServerEntityMetadata entityMetaPacket = new WrapperPlayServerEntityMetadata(
                    entityId,
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


            if (glassItem != null) {
                final WrapperPlayServerSpawnEntity glassSpawnPacket = new WrapperPlayServerSpawnEntity(
                        glassEntityId,
                        glassEntityUuid,
                        SpigotConversionUtil.fromBukkitEntityType(EntityType.ARMOR_STAND),
                        SpigotConversionUtil.fromBukkitLocation(currentLocation.clone().add(0, -config.getOverlayOffset(), 0)),
                        0.0F,
                        0,
                        null
                );

                // Dress the entity
                final WrapperPlayServerEntityEquipment glassEquipPacket = new WrapperPlayServerEntityEquipment(
                        glassEntityId,
                        List.of(new Equipment(
                                com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET,
                                glassItem
                        ))
                );

                // Protect the entity
                final WrapperPlayServerEntityMetadata glassMetaPacket = new WrapperPlayServerEntityMetadata(
                        glassEntityId,
                        List.of(
                                new EntityData( // Invisible
                                        0,
                                        EntityDataTypes.BYTE,
                                        (byte) 0x20
                                )
                        )
                );

                PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassSpawnPacket);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassEquipPacket);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassMetaPacket);
            }

            if (config.getScale() != 1.0) {
                // We only need to send an Attribute packet if the scale is not 1.0 (None-Default)
                final WrapperPlayServerUpdateAttributes entityAttributes = createAttributePacket(entityId, config.getScale());
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityAttributes);

                if (!config.getOverlayMaterial().isEmpty() && !config.getOverlayMaterial().equals("AIR")) {
                    final WrapperPlayServerUpdateAttributes glassAttributes = createAttributePacket(glassEntityId, config.getScale() + 0.3);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassAttributes);
                }
            } else if (glassItem != null) {
                final WrapperPlayServerUpdateAttributes glassAttributes = createAttributePacket(glassEntityId, 1.3);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassAttributes);
            }

        }).thenAcceptAsync(v -> {
            // May be needed to await completion and then execute after, but im
            // not sure of the behavior when entities are modified before the client knows they exist.
            // May be something to address later in a PR
        });

        hologram.getVisibilityManager().showTo(player);
    }

    private @NotNull WrapperPlayServerUpdateAttributes createAttributePacket(int entity, double scale) {
        final WrapperPlayServerUpdateAttributes.Property scalePacket = new WrapperPlayServerUpdateAttributes.Property(
                Attributes.SCALE,
                scale,
                new ArrayList<>()
        );

        final WrapperPlayServerUpdateAttributes.Property interactionRange = new WrapperPlayServerUpdateAttributes.Property(
                Attributes.ENTITY_INTERACTION_RANGE,
                scale,
                new ArrayList<>()
        );

        return new WrapperPlayServerUpdateAttributes(entity, List.of(scalePacket, interactionRange));
    }

    public void teleport(Location location) {
        CompletableFuture.runAsync(() -> {
            final WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
            if (glassItem != null) {
                final WrapperPlayServerEntityTeleport glassTeleportPacket = new WrapperPlayServerEntityTeleport(glassEntityId, SpigotConversionUtil.fromBukkitLocation(location.clone().add(0, -config.getOverlayOffset(), 0)), false);
                getCurrentLocation().getWorld().getPlayers().forEach(player -> {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, teleportPacket);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassTeleportPacket);
                });
            } else {
                getCurrentLocation().getWorld().getPlayers().forEach(player -> {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, teleportPacket);
                });
            }
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

        final WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityId);

        if (glassItem != null) {
            final WrapperPlayServerDestroyEntities destroyGlassPacket = new WrapperPlayServerDestroyEntities(glassEntityId);
            getCurrentLocation().getWorld().getPlayers().forEach(player -> {
                if (player.isOnline()) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyGlassPacket);
                }
            });
        } else {
            currentLocation.getWorld().getPlayers().forEach(player -> {
                if (player.isOnline()) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
                }
            });
        }

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
