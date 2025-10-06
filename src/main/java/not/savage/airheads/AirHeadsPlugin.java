package not.savage.airheads;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.NonNull;
import not.savage.airheads.commands.CmdAirHeads;
import not.savage.airheads.config.AirHeadConfig;
import not.savage.airheads.config.AirHeadsConfig;
import not.savage.airheads.listener.PacketInterceptListener;
import not.savage.airheads.listener.PlayerListener;
import not.savage.airheads.utility.ConfigBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The main plugin class for AirHeads.
 */
public class AirHeadsPlugin extends JavaPlugin implements AirHeadAPI {

    private static final String CONFIG_FILE_NAME = "config.yml";

    @Getter private AirHeadsConfig airHeadsConfig;
    @Getter private PacketEntityCache packetEntityCache;

    @Override
    public void onLoad() {
        getLogger().info("Initializing ShockAirHeads Plugin!");
        try {
            Class.forName("not.savage.airheads.shade.com.github.retrooper.packetevents.PacketEventsAPI");
            getLogger().info("Using shaded PacketEvents.");
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
            PacketEvents.getAPI().load();
        } catch (ClassNotFoundException ignored) {
            getLogger().info("Using non-shaded PacketEvents (server dependency).");
        }
    }

    @Override
    public void onEnable() {
        final Instant start = Instant.now();
        getLogger().info("Loading ShockAirHeads Plugin!");

        loadConfig();
        getLogger().info("Loaded ShockAirHeads config from " + getDataFolder().getAbsolutePath() + "/" + CONFIG_FILE_NAME);

        getLogger().info("Initializing PacketEvents...");
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketInterceptListener(this), PacketListenerPriority.NORMAL);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLogger().info("Setting up packet based entities...");
        this.packetEntityCache = new PacketEntityCache(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        spawnFakeEntities();

        registerCommands();
        getLogger().info("Registered `/airheads reload` command.");

        getLogger().info("ShockAirHeads Plugin loaded in %dms".formatted(Duration.between(start, Instant.now()).toMillis()));
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Shock ShockAirHeads Plugin!");
        getPacketEntityCache().clear();
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        PacketEvents.getAPI().terminate();
    }

    /**
     * Reload the config & respawn airheads.
     */
    public void reloadPlugin(boolean reloadConfig) {
        getLogger().info("Reloading ShockAirHeads Plugin!");
        if (reloadConfig) loadConfig();
        packetEntityCache.clear();
        spawnFakeEntities();
        Bukkit.getOnlinePlayers().forEach(player -> packetEntityCache.showWorld(player));
    }

    private void registerCommands() {
        final PluginCommand command = getCommand("airheads");
        final CmdAirHeads handler = new CmdAirHeads(this);
        if (command == null) {
            getLogger().severe("Failed to register command.");
            return;
        }

        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    private void loadConfig() {
        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            getLogger().severe("Failed to create data folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        airHeadsConfig = new ConfigBuilder<>(AirHeadsConfig.class)
                .withPath(new File(getDataFolder(), CONFIG_FILE_NAME).toPath())
                .build();
    }

    public void saveUpdates() {
        CompletableFuture.runAsync(() -> {
            new ConfigBuilder<>(AirHeadsConfig.class)
                    .withPath(new File(getDataFolder(), CONFIG_FILE_NAME).toPath())
                    .save(this.airHeadsConfig);
        });
    }

    /**
     * Spawn / Setup the entity state, ready to be dispatched as players join.
     */
    private void spawnFakeEntities() {
        final long offsetTicks = airHeadsConfig.getFloatAnimationOffsetTicks();
        long offset = offsetTicks;
        for (Map.Entry<String, AirHeadConfig> airHead : airHeadsConfig.getAirHeads().entrySet()) {
            AirHeadEntity airHeadEntity = new AirHeadEntity(this, airHead.getKey(), airHead.getValue(), offset);
            packetEntityCache.addEntity(airHeadEntity.getEntityId(), airHeadEntity);
            offset += offsetTicks;
        }
    }

    @Override
    public AirHeadEntity spawnAirHead(JavaPlugin plugin, String customId, AirHeadBuilder builder, boolean persistent) {
        AirHeadEntity airHeadEntity = new AirHeadEntity(this, customId, builder.airHeadConfig, 0);
        packetEntityCache.addEntity(airHeadEntity.getEntityId(), airHeadEntity);
        if (persistent) {
            airHeadsConfig.getAirHeads().put(customId, builder.airHeadConfig);
            saveUpdates();
        }
        return airHeadEntity;
    }

    @Override
    public Optional<AirHeadEntity> getAirHeadEntity(@NonNull String airHeadId) {
        return Optional.ofNullable(packetEntityCache.getByAirHeadName(airHeadId));
    }

    @Override
    public boolean removeAirHead(@NonNull String airHeadId) {
        AirHeadEntity entity = packetEntityCache.getByAirHeadName(airHeadId);
        if (entity != null) {
            entity.remove();
            packetEntityCache.removeEntityByName(airHeadId);
            airHeadsConfig.getAirHeads().remove(airHeadId);
            saveUpdates();
            return true;
        }
        return false;
    }
}
