package not.savage.airheads.listener;

import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    private final AirHeadsPlugin plugin;

    public PlayerListener(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!e.getTo().getWorld().getName().equals(e.getFrom().getWorld().getName())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPacketEntityCache().showWorld(e.getPlayer()), 1);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPacketEntityCache().showWorld(e.getPlayer()), 1);
    }
}
