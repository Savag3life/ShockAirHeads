package not.savage.airheads.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadEntity;
import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Protects the armor stands created by the plugin from being interacted with or damaged.
 */
public class ArmorStandProtection implements Listener {

    private final AirHeadsPlugin plugin;

    public ArmorStandProtection(AirHeadsPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
        if (isProtectedArmorStand(event.getRightClicked())) {
            event.setCancelled(true);
            this.plugin.findAirHeadByEntity((ArmorStand) event.getRightClicked())
                    .ifPresent(airHead -> {

                        airHead.getConfig()
                                .getInteractCommands()
                                .forEach(
                                        cmd -> Bukkit.dispatchCommand(event.getPlayer(), cmd)
                                );

                        airHead.getConfig()
                                .getConsoleCommands()
                                .forEach(
                                        cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", event.getPlayer().getName()))
                                );

                        if (!airHead.getConfig().getInteractMessage().isEmpty()) {
                            airHead.getConfig().getInteractMessage().forEach(line ->
                                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(line)));
                        }


                        if (airHead.getConfig().getSoundSettings().isEnabled()) {
                            event.getPlayer().playSound(
                                    event.getPlayer(),
                                    airHead.getConfig().getSoundSettings().getSound(),
                                    airHead.getConfig().getSoundSettings().getVolume(),
                                    airHead.getConfig().getSoundSettings().getPitch()
                            );
                        }

                    });
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(isProtectedArmorStand(event.getRightClicked()));
    }

    @EventHandler
    public void onArmorStandDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(isProtectedArmorStand(event.getEntity()));
    }

    /**
     * Check if the armor stand is protected by the plugin
     * @param armorStand The armor stand to check
     * @return True if the armor stand is protected.
     */
    private boolean isProtectedArmorStand(Entity armorStand) {
        return armorStand instanceof ArmorStand &&
                armorStand.getPersistentDataContainer().has(AirHeadEntity.KEY, PersistentDataType.BOOLEAN);
    }
}
