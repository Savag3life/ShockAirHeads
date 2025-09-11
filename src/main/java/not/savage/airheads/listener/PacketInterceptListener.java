package not.savage.airheads.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadEntity;
import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class listens for packet events and handles interactions with air heads.
 * It is responsible for executing commands and sending messages when a player interacts with an air head.
 */
public class PacketInterceptListener implements PacketListener {

    private final AirHeadsPlugin plugin;
    private final String PLAYER_TAG = "%player%";

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
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return; // We only care about interact entity packets
        final WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
        final int entityId = packet.getEntityId(); // EntityID is our fake entity's ID
        final AirHeadEntity airHead = plugin.getPacketEntityCache().getEntityByEntityId(entityId);
        if (airHead != null) { // If the entity is a registered air head - We cancel and handle it.
            event.setCancelled(true);
            final Player player = event.getPlayer();
            syncExecute(player, airHead, packet.getAction());
        }
    }

    /**
     * Synchronously execute commands and send messages based on the interaction action.
     * @param player the player who interacted with the air head
     * @param airHead the air head entity that was interacted with
     * @param action the type of interaction action
     */
    private void syncExecute(final Player player, final AirHeadEntity airHead, final WrapperPlayClientInteractEntity.InteractAction action) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            // Left-Click Interaction
            if (action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                handleLeftClick(player, airHead);
            }

            // Right-Click Interaction
            if (action == WrapperPlayClientInteractEntity.InteractAction.INTERACT ||
                    action == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                handleRightClick(player, airHead);
            }

            // Commands dispatched as the Interacting player (non-specific)
            airHead.getConfig().getInteractSettings()
                    .getInteractCommands()
                    .forEach(
                            cmd -> Bukkit.dispatchCommand(
                                    player,
                                    cmd.replace(PLAYER_TAG, player.getName())
                            )
                    );

            // Commands dispatched as the Console (non-specific)
            airHead.getConfig().getInteractSettings()
                    .getConsoleCommands()
                    .forEach(cmd -> Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    cmd.replace(PLAYER_TAG, player.getName())
                            )
                    );

            // Interact Message (non-specific)
            if (!airHead.getConfig().getInteractSettings().getInteractMessage().isEmpty()) {
                airHead.getConfig().getInteractSettings()
                        .getInteractMessage()
                        .forEach(line -> player.sendMessage(MiniMessage.miniMessage().deserialize(line)));
            }

            playInteractSound(player, airHead);
            trySendConnectMessage(player, airHead);
        });
    }

    /**
     * Handle left-click specific interactions with the air head entity.
     * @param player the player who interacted with the air head
     * @param airHead the air head entity that was interacted with
     */
    private void handleLeftClick(Player player, AirHeadEntity airHead) {
        airHead.getConfig().getInteractSettings()
                .getLeftClickCommands()
                .forEach(cmd -> Bukkit.dispatchCommand(
                                player,
                                cmd.replace(PLAYER_TAG, player.getName())
                        )
                );

        airHead.getConfig().getInteractSettings()
                .getLeftClickConsoleCommands()
                .forEach(cmd -> Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                cmd.replace(PLAYER_TAG, player.getName())
                        )
                );
    }

    /**
     * Handle right-click specific interactions with the air head entity.
     * @param player the player who interacted with the air head
     * @param airHead the air head entity that was interacted with
     */
    private void handleRightClick(Player player, AirHeadEntity airHead) {
        airHead.getConfig().getInteractSettings()
                .getRightClickCommands()
                .forEach(cmd -> Bukkit.dispatchCommand(
                                player,
                                cmd.replace(PLAYER_TAG, player.getName())
                        )
                );

        airHead.getConfig().getInteractSettings()
                .getRightClickConsoleCommands()
                .forEach(cmd -> Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                cmd.replace(PLAYER_TAG, player.getName())
                        )
                );
    }

    /**
     * Play a sound to the player upon interaction with the air head, if enabled in the configuration.
     * @param player the player to play the sound to
     * @param airHead the air head entity containing the sound settings
     */
    private void playInteractSound(Player player, AirHeadEntity airHead) {
        if (airHead.getConfig().getInteractSettings().getSoundSettings().isEnabled() &&
                airHead.getConfig().getInteractSettings().getSoundSettings().sound() != null) {
            player.playSound(
                    player,
                    airHead.getConfig().getInteractSettings().getSoundSettings().sound(),
                    airHead.getConfig().getInteractSettings().getSoundSettings().getVolume(),
                    airHead.getConfig().getInteractSettings().getSoundSettings().getPitch()
            );
        }
    }

    /**
     * Attempt to send a player to another server via BungeeCord plugin channels (and Velocity)
     * @param player the player to send
     * @param airHead the air head entity containing the target server information
     */
    private void trySendConnectMessage(final Player player, final AirHeadEntity airHead) {
        if (!airHead.getConfig().getInteractSettings().getSendTo().isEmpty()) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();

            try {
                out.writeUTF("Connect");
                out.writeUTF(airHead.getConfig().getInteractSettings().getSendTo());
                player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            } catch (Exception ignored) {
                player.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                "<red>Error sending to server: " + airHead.getConfig().getInteractSettings().getSendTo() + "."
                        )
                );
            }
        }
    }
}
