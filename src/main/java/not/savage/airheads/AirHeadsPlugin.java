package not.savage.airheads;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import gg.optimalgames.hologrambridge.HologramBridge;
import lombok.Getter;
import not.savage.airheads.commands.CmdAirHeads;
import not.savage.airheads.config.AirHead;
import not.savage.airheads.config.AirHeadsConfig;
import not.savage.airheads.config.Config;
import not.savage.airheads.listener.PacketInterceptListener;
import not.savage.airheads.listener.PlayerListener;
import not.savage.airheads.utility.ConfigBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * The main plugin class for AirHeads.
 */
public class AirHeadsPlugin extends JavaPlugin {

    @Getter private Config airHeadsConfig;
    @Getter private PacketEntityCache packetEntityCache;

    @Override
    public void onEnable() {
        final Instant start = Instant.now();
        getLogger().info("Loading ShockAirHeads Plugin!");

        if (!setupHologramBridge()) {
            getLogger().severe("Failed to setup HologramBridge!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        loadConfig();
        getLogger().info("Loaded ShockAirHeads config from " + getDataFolder().getAbsolutePath() + "/airheads.yml");

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
    public void reloadPlugin() {
        getLogger().info("Reloading ShockAirHeads Plugin!");
        loadConfig();
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

        if (new File(getDataFolder(), "config.yml").exists()) {
            migrateConfig();
        }

        if (airHeadsConfig == null) {
            airHeadsConfig = new ConfigBuilder<>(Config.class)
                    .withPath(new File(getDataFolder(), "airheads.yml").toPath())
                    .build();
        }
    }

    public void saveUpdates() {
        CompletableFuture.runAsync(() -> {
            new ConfigBuilder<>(Config.class)
                    .withPath(new File(getDataFolder(), "airheads.yml").toPath())
                    .save(this.airHeadsConfig);
        });
    }

    /**
     * Spawn / Setup the entity state, ready to be dispatched as players join.
     */
    private void spawnFakeEntities() {
        final long offsetTicks = airHeadsConfig.getFloatAnimationOffsetTicks();
        long offset = offsetTicks;
        for (AirHead airHead : airHeadsConfig.getAirHeads().values()) {
            AirHeadEntity airHeadEntity = new AirHeadEntity(this, airHead, offset);
            packetEntityCache.addEntity(airHeadEntity.getEntityId(), airHeadEntity);
            offset += offsetTicks;
        }
    }

    /**
     * Distribute Shaded & Unshaded versions of the plugin.
     * If HologramBridge is not found, initialize it, but check
     * to make sure we have it shaded first.
     */
    private boolean setupHologramBridge() {
        final Plugin hologramBridge = Bukkit.getPluginManager().getPlugin("HologramBridge");
        if (hologramBridge != null && hologramBridge.isEnabled()) {
            getLogger().info("HologramBridge found!");
            return true;
        } else {
            try {
                Class.forName("not.savage.shade.hologrambridge.HologramAPI");
                initializeHologramBridge();
                return true;
            } catch (ClassNotFoundException e) {
                getLogger().warning(
                        "You have installed the wrong version of Shock AirHeads! The AirHeads version" +
                        "installed does not include HologramBridge, you are meant to have it installed yourself... " +
                        "if you dont want that, you can just download the version which includes it from https://github.com/Savag3life/ShockAirHeads"
                );
                return false;
            }
        }
    }

    /**
     * ClassNotFoundException is thrown when invoked, so we can safely
     * initialize the bridge here without worrying about it being
     * shaded in the wrong version.
     * Access {@link gg.optimalgames.hologrambridge.HologramAPI}
     */
    private void initializeHologramBridge() {
        getLogger().info("Initializing internal HologramBridge...");
        new HologramBridge(this, false);
    }

    /**
     * Migrate the original config.yml to the new airheads.yml format.
     * Using a map instead of list to define each airhead config section.
     */
    @SuppressWarnings("deprecation")
    private void migrateConfig() {
        getLogger().info("Found old config.yml, converting to airheads.yml");
        this.airHeadsConfig = new Config();
        final AirHeadsConfig oldConfig = new ConfigBuilder<>(AirHeadsConfig.class)
                .withPath(new File(getDataFolder(), "config.yml").toPath())
                .build();

        if (!oldConfig.getAirHeads().isEmpty()) {
            int x = 0;
            for (AirHead airHead : oldConfig.getAirHeads()) {
                airHeadsConfig.getAirHeads().put("name-" + x, airHead);
                x++;
            }

            new ConfigBuilder<>(Config.class)
                    .withPath(new File(getDataFolder(), "airheads.yml").toPath())
                    .save(airHeadsConfig);

            getLogger().info("Converted old config.yml to airheads.yml");

            File backup = new File(getDataFolder(), "config.yml.backup");
            if (backup.exists()) {
                backup.delete();
            }

            if (!new File(getDataFolder(), "config.yml").renameTo(backup)) {
                getLogger().warning("Failed to backup old config.yml");
            } else {
                getLogger().info("Backed up old config.yml to config.yml.backup");
            }

            if (!new File(getDataFolder(), "config.yml").delete()) {
                getLogger().warning("Failed to delete old config.yml");
            } else {
                getLogger().info("Deleted old config.yml");
            }
        }
    }
}
