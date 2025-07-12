package not.savage.airheads.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadEntity;
import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This class listens for packet events and handles interactions with air heads.
 * It is responsible for executing commands and sending messages when a player interacts with an air head.
 */
public class PacketInterceptListener implements PacketListener {

    private final AirHeadsPlugin plugin;

    public PacketInterceptListener(final AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onUserLogin(@NotNull UserLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CompletableFuture.runAsync(() -> plugin.getPacketEntityCache().showWorld(event.getPlayer()));
        }, 5L);
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
                        .getInteractSettings()
                        .getInteractCommands()
                        .forEach(
                                cmd -> Bukkit.dispatchCommand(player, cmd)
                        );

                // Left-Click Interaction
                if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                    airHead.getConfig()
                            .getInteractSettings()
                            .getLeftClickCommands()
                            .forEach(cmd -> Bukkit.dispatchCommand(
                                            player,
                                            cmd.replace("%player%", player.getName())
                                    )
                            );

                    airHead.getConfig()
                            .getInteractSettings()
                            .getLeftClickConsoleCommands()
                            .forEach(cmd -> Bukkit.dispatchCommand(
                                            Bukkit.getConsoleSender(),
                                            cmd.replace("%player%", player.getName())
                                    )
                            );
                }

                // Right-Click Interaction
                if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT ||
                        packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                    airHead.getConfig()
                            .getInteractSettings()
                            .getRightClickCommands()
                            .forEach(cmd -> Bukkit.dispatchCommand(
                                            player,
                                            cmd.replace("%player%", player.getName())
                                    )
                            );

                    airHead.getConfig()
                            .getInteractSettings()
                            .getRightClickConsoleCommands()
                            .forEach(cmd -> Bukkit.dispatchCommand(
                                            Bukkit.getConsoleSender(),
                                            cmd.replace("%player%", player.getName())
                                    )
                            );
                }

                airHead.getConfig()
                        .getInteractSettings()
                        .getConsoleCommands()
                        .forEach(cmd -> Bukkit.dispatchCommand(
                                        Bukkit.getConsoleSender(),
                                        cmd.replace("%player%", player.getName())
                                )
                        );
                if (!airHead.getConfig().getInteractSettings()
                        .getInteractMessage().isEmpty()) {
                    airHead.getConfig()
                            .getInteractSettings()
                            .getInteractMessage()
                            .forEach(line -> player.sendMessage(MiniMessage.miniMessage().deserialize(line)));
                }


                if (airHead.getConfig().getInteractSettings()
                        .getSoundSettings().isEnabled()) {
                    player.playSound(
                            player,
                            Objects.requireNonNull(RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(airHead.getConfig().getInteractSettings().getSoundSettings().getSound())),
                            airHead.getConfig().getInteractSettings().getSoundSettings().getVolume(),
                            airHead.getConfig().getInteractSettings().getSoundSettings().getPitch()
                    );
                }

                if (!airHead.getConfig().getInteractSettings().getSendTo().isEmpty()) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();

                    try {
                        out.writeUTF("Connect");
                        out.writeUTF(airHead.getConfig().getInteractSettings().getSendTo());
                        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    } catch (Exception e) {
                        player.sendMessage(
                                MiniMessage.miniMessage().deserialize(
                                        "<red>Error sending to server: " + airHead.getConfig().getInteractSettings().getSendTo() + "."
                                )
                        );
                    }
                }
            });
        }
    }
}
