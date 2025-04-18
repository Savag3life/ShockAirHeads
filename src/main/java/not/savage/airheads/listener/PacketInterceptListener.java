package not.savage.airheads.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadEntity;
import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketInterceptListener implements PacketListener {

    private final AirHeadsPlugin plugin;

    public PacketInterceptListener(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        plugin.getPacketEntityCache().showWorld(event.getPlayer());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;
        final WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
        final int entityId = packet.getEntityId();
        final AirHeadEntity airHead = plugin.getPacketEntityCache().getEntityByEntityId(entityId);
        if (airHead != null) {
            event.setCancelled(true);

            final Player player = event.getPlayer();
            // PacketReceiveEvent is async, so we switch back to sync
            Bukkit.getScheduler().runTask(plugin, () -> {
                airHead.getConfig()
                        .getInteractCommands()
                        .forEach(
                                cmd -> Bukkit.dispatchCommand(event.getPlayer(), cmd)
                        );

                airHead.getConfig()
                        .getConsoleCommands()
                        .forEach(cmd -> Bukkit.dispatchCommand(
                                        Bukkit.getConsoleSender(),
                                        cmd.replace("%player%", player.getName())
                                )
                        );

                if (!airHead.getConfig().getInteractMessage().isEmpty()) {
                    airHead.getConfig().getInteractMessage()
                            .forEach(line -> player.sendMessage(MiniMessage.miniMessage().deserialize(line)));
                }


                if (airHead.getConfig().getSoundSettings().isEnabled()) {
                    player.playSound(
                            player,
                            airHead.getConfig().getSoundSettings().getSound(),
                            airHead.getConfig().getSoundSettings().getVolume(),
                            airHead.getConfig().getSoundSettings().getPitch()
                    );
                }
            });
        }
    }
}
