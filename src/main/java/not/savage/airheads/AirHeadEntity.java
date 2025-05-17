package not.savage.airheads;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import not.savage.airheads.config.AirHeadConfig;
import not.savage.airheads.hologram.TextDisplayHologram;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class AirHeadEntity {

    // Armor Stand AABB is 0.5 x 0.5 x 1.975
    private final float DEFAULT_ARMOR_STAND_HEIGHT = 1.975F;

    private final AirHeadsPlugin plugin;
    private final AirHeadConfig config;

    private final int entityId;
    private final UUID entityUuid;

    private final int glassEntityId;
    private final UUID glassEntityUuid;

    private final TextDisplayHologram textDisplay;

    private final String name;

    @Setter private Location currentLocation;
    private final ItemStack headItem;
    private final ItemStack glassItem;
    private final int floatTask;
    private final long activeAfter;

    /**
     * This plugin does not currently provide API support for
     * using AirHeads outside the configs of the commands & the config.
     * @param config The AirHead configuration
     * @param delayedTicks The number of ticks to delay the AirHead from spawning.
     */
    public AirHeadEntity(final AirHeadsPlugin plugin, final String name, final AirHeadConfig config, final long delayedTicks) {
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

        if (!config.getAppearanceSettings().getHeadTexture().equals("%player%")) {
            final org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
            final SkullMeta meta = (SkullMeta) head.getItemMeta();
            Heads.setBase64ToSkullMeta(config.getAppearanceSettings().getHeadTexture(), meta);
            head.setItemMeta(meta);
            this.headItem = SpigotConversionUtil.fromBukkitItemStack(head);
        } else {
            this.headItem = null;
        }

        if (!config.getAppearanceSettings().getOverlayMaterial().isAir()) {
            this.glassItem = SpigotConversionUtil.fromBukkitItemStack(new org.bukkit.inventory.ItemStack(config.getAppearanceSettings().getOverlayMaterial(), 1));
        } else {
            this.glassItem = null;
        }

        this.textDisplay = new TextDisplayHologram(plugin, config.getHologramTextDisplaySettings(), SpigotReflectionUtil.generateEntityId(), UUID.randomUUID(), currentLocation.getWorld());
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

            WrapperPlayServerEntityEquipment entityEquipPacket;
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
                        SpigotConversionUtil.fromBukkitLocation(currentLocation.clone().add(0, -config.getAppearanceSettings().getOverlayOffset(), 0)),
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

            if (config.getAppearanceSettings().getScale() != 1.0) {
                // We only need to send an Attribute packet if the scale is not 1.0 (None-Default)
                final WrapperPlayServerUpdateAttributes entityAttributes = createScalerPacket(entityId, config.getAppearanceSettings().getScale());
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityAttributes);

                if (glassItem != null) {
                    final WrapperPlayServerUpdateAttributes glassAttributes = createScalerPacket(glassEntityId, config.getAppearanceSettings().getScale() + 0.3);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassAttributes);
                }
            } else if (glassItem != null) {
                final WrapperPlayServerUpdateAttributes glassAttributes = createScalerPacket(glassEntityId, 1.3);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, glassAttributes);
            }

            textDisplay.spawn(player, currentLocation.clone().add(0, trueHeight(), 0));

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private @NotNull WrapperPlayServerUpdateAttributes createScalerPacket(int entity, double scale) {
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

    public void teleport(final Location location) {
        CompletableFuture.runAsync(() -> {
            textDisplay.teleport(location.clone().add(0, trueHeight(), 0));
            final WrapperPlayServerEntityTeleport mainEntityTeleport = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
            final WrapperPlayServerEntityTeleport overlayEntityTeleport = glassItem == null ? null :
                    new WrapperPlayServerEntityTeleport(
                            glassEntityId,
                            SpigotConversionUtil.fromBukkitLocation(location.clone().add(0, -config.getAppearanceSettings().getOverlayOffset(), 0)),
                            false
                    );

            getCurrentLocation().getWorld().getPlayers().forEach(player -> {
                if (player.isOnline()) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, mainEntityTeleport);
                    if (overlayEntityTeleport != null) {
                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, overlayEntityTeleport);
                    }
                }
            });
        });

        setCurrentLocation(location);
    }

    /**
     * Remove all the AirHead components from the world.
     */
    public void remove() {
        if (floatTask != -1) {
            Bukkit.getScheduler().cancelTask(floatTask);
        }
        final int[] entitiesToRemove = new int[glassItem != null ? 3 : 2];
        entitiesToRemove[0] = entityId;
        entitiesToRemove[1] = textDisplay.getEntityId();
        if (glassItem != null) {
            entitiesToRemove[2] = glassEntityId;
        }
        textDisplay.getHologramUpdateTask().cancel();
        final WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entitiesToRemove);
        getCurrentLocation().getWorld().getPlayers().forEach(player -> {
            if (player.isOnline()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
            }
        });
    }

    public double trueHeight() {
        final double configScale = config.getAppearanceSettings().getScale();
        if (configScale == 1.0) return DEFAULT_ARMOR_STAND_HEIGHT;
        if (configScale < 1.0) {
            return DEFAULT_ARMOR_STAND_HEIGHT * configScale;
        }
        return DEFAULT_ARMOR_STAND_HEIGHT + configScale;
    }
}
