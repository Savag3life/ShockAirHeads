package not.savage.airheads;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import lombok.NonNull;
import not.savage.airheads.commands.CmdAirHeads;
import not.savage.airheads.config.AirHead;
import not.savage.airheads.config.AirHeadConfig;
import not.savage.airheads.config.AirHeadsConfig;
import not.savage.airheads.config.Config;
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
    public void onEnable() {
        final Instant start = Instant.now();
        getLogger().info("Loading ShockAirHeads Plugin!");

        loadConfig();
        getLogger().info("Loaded ShockAirHeads config from " + getDataFolder().getAbsolutePath() + "/" + CONFIG_FILE_NAME);

        getLogger().info("Setting up packet based entities...");
        this.packetEntityCache = new PacketEntityCache(this);

        PacketEvents.getAPI().getEventManager().registerListener(new PacketInterceptListener(this), PacketListenerPriority.NORMAL);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        spawnFakeEntities();

        registerCommands();
        getLogger().info("Registered `/airheads reload` command.");

        getLogger().info(String.format("ShockAirHeads Plugin loaded in %sms", Duration.between(start, Instant.now()).toMillis()));
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Shock ShockAirHeads Plugin!");
        getPacketEntityCache().clear();
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

        if (new File(getDataFolder(), "airheads.yml").exists()) {
            migrateConfig();
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

    /**
     * 1.0.0 was "config.yml"
     * 1.5.0 migrated config.yml -> airheads.yml (migration removed, now requires config reset)
     * 2.0.0 migrates airheads.yml -> config.yml (Auto migration)
     */
    private void migrateConfig() {
        getLogger().info("Found old config.yml, converting to airheads.yml");
        this.airHeadsConfig = new AirHeadsConfig();
        final Config oldConfig = new ConfigBuilder<>(Config.class)
                .withPath(new File(getDataFolder(), "airheads.yml").toPath())
                .build();

        if (!oldConfig.getAirHeads().isEmpty()) {
            for (Map.Entry<String, AirHead> airHead : oldConfig.getAirHeads().entrySet()) {

                final AirHeadConfig updatedConfig = new AirHeadConfig(airHead.getValue());
                this.airHeadsConfig.getAirHeads().put(airHead.getKey(), updatedConfig);
            }

            new ConfigBuilder<>(AirHeadsConfig.class)
                    .withPath(new File(getDataFolder(), CONFIG_FILE_NAME).toPath())
                    .save(airHeadsConfig);

            getLogger().info("Converted old airheads.yml to config.yml");

            File backup = new File(getDataFolder(), "airheads.yml.backup");
            if (backup.exists()) {
                backup.delete();
            }

            if (!new File(getDataFolder(), "airheads.yml").renameTo(backup)) {
                getLogger().warning("Failed to backup old config.yml");
            } else {
                getLogger().info("Backed up old config.yml to config.yml.backup");
            }

            if (!new File(getDataFolder(), "airheads.yml").delete()) {
                getLogger().warning("Failed to delete old config.yml");
            } else {
                getLogger().info("Deleted old config.yml");
            }
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
