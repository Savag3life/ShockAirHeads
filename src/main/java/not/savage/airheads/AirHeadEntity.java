package not.savage.airheads;

import gg.optimalgames.hologrambridge.HologramAPI;
import gg.optimalgames.hologrambridge.hologram.Hologram;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.config.AirHead;
import not.savage.airheads.tasks.AirHeadAnimationTask;
import not.savage.airheads.utility.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents an actual "AirHead" in-game & all of its various components.
 * The Hologram above the head is provided by {@link gg.optimalgames.hologrambridge.HologramBridge}
 * The float & rotation animation is handled in {@link AirHeadAnimationTask}
 * Do not keep long-term references to this object, as it will be removed when the plugin is reloaded.
 * Use {@link AirHeadsPlugin#findAirHeadByEntity(ArmorStand)} to detect if an ArmorStand is an AirHead.
 */
@Getter
public class AirHeadEntity {

    public static final NamespacedKey KEY = new NamespacedKey("airheads", "armor_stand");

    private final AirHead config;

    private final Location location;
    private ArmorStand head;
    private int floatTask = -1;
    private Hologram hologram;
    private final long activeAfter;

    /**
     * This plugin does not currently provide API support for
     * using AirHeads outside the configs of the commands & the config.
     * TODO: Add API support for creating AirHeads via a developer API.
     * @param config The AirHead configuration
     * @param delayedTicks The number of ticks to delay the AirHead from spawning.
     */
    protected AirHeadEntity(AirHead config, long delayedTicks) {
        this.config = config;
        this.location = config.getLocation();
        this.activeAfter = System.currentTimeMillis() + (delayedTicks * 50);
    }

    /**
     * Initialize the AirHead entities in the world.
     * This will spawn the ArmorStand, set the head texture,
     * start the float animation, and spawn the hologram.
     */
    public void spawnAirHead() {
        head = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        head.setGravity(false);
        head.setBasePlate(false);
        head.setInvisible(true);
        head.setInvisible(true);
        head.setArms(false);
        head.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        Heads.setBase64ToSkullMeta(config.getHeadTexture(), meta);
        playerHead.setItemMeta(meta);

        head.setItem(EquipmentSlot.HEAD, playerHead);

        this.floatTask = new AirHeadAnimationTask(location, this, activeAfter)
                .runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 1)
                .getTaskId();

        this.hologram = HologramAPI.createHologram(location.add(0, 3, 0));
        for (String line : config.getHologramText()) {
            hologram.appendTextLine(MiniMessage.miniMessage().deserialize(line));
        }
    }

    /**
     * Remove all the AirHead components from the world.
     */
    public void remove() {
        if (floatTask != -1) {
            Bukkit.getScheduler().cancelTask(floatTask);
        }

        if (head != null) {
            head.remove();
        }

        if (hologram != null) {
            hologram.delete();
        }
    }
}
