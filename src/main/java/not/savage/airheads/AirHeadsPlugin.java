package not.savage.airheads;

import gg.optimalgames.hologrambridge.HologramBridge;
import not.savage.airheads.commands.CmdAirHeads;
import not.savage.airheads.config.AirHead;
import not.savage.airheads.config.AirHeadsConfig;
import not.savage.airheads.listener.ArmorStandProtection;
import not.savage.airheads.utility.ConfigBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main plugin class for AirHeads.
 */
public class AirHeadsPlugin extends JavaPlugin {

    private AirHeadsConfig airHeadsConfig;
    private final List<AirHeadEntity> entities = new ArrayList<>();

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
        getLogger().info("Loaded ShockAirHeads config from " + getDataFolder().getAbsolutePath() + "/config.yml");

        if (airHeadsConfig.isRunCleanupOnStart()) {
            getLogger().info("Cleaning up any stray AirHeads...");
            Bukkit.getWorlds().forEach(world -> {
                world.getEntities().forEach(entity -> {
                    if (entity.getPersistentDataContainer().has(AirHeadEntity.KEY, PersistentDataType.BOOLEAN)) {
                        getLogger().info("Removing stray AirHead entity... " +
                                "(x: " + entity.getLocation().getBlockX() +
                                ", y: " + entity.getLocation().getBlockY() +
                                ", z: " + entity.getLocation().getBlockZ() + ")"
                        );
                        entity.remove();
                    }
                });
            });
        }

        spawnEntities();
        getLogger().info(String.format("Spawned all AirHeads (%d)", entities.size()));

        registerCommands();
        getLogger().info("Registered `/airheads reload` command.");

        new ArmorStandProtection(this);

        getLogger().info(String.format("ShockAirHeads Plugin loaded in %sms", Duration.between(start, Instant.now()).toMillis()));
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Shock ShockAirHeads Plugin!");
        entities.forEach(AirHeadEntity::remove);
    }

    /**
     * Find an AirHeadEntity by the armor stand entity.
     * @param armorStand The armor stand to search for.
     * @return The AirHeadEntity if found.
     */
    public Optional<AirHeadEntity> findAirHeadByEntity(ArmorStand armorStand) {
        return entities.stream()
                .filter(entity -> entity.getHead().getUniqueId().equals(armorStand.getUniqueId()))
                .findFirst();
    }

    /**
     * Reload the config & respawn airheads.
     */
    public void reloadPlugin() {
        getLogger().info("Reloading ShockAirHeads Plugin!");
        loadConfig();
        entities.forEach(AirHeadEntity::remove);
        entities.clear();
        spawnEntities();
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
        if (!getDataFolder().exists()) {
            boolean failed = getDataFolder().mkdir();
            if (failed) {
                getLogger().severe("Failed to create data folder!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        airHeadsConfig = new ConfigBuilder<>(AirHeadsConfig.class)
                .withPath(new File(getDataFolder(), "config.yml").toPath())
                .build();
    }

    private void spawnEntities() {
        final long offsetTicks = airHeadsConfig.getFloatAnimationOffsetTicks();
        long offset = offsetTicks;
        for (AirHead airHead : airHeadsConfig.getAirHeads()) {
            AirHeadEntity airHeadEntity = new AirHeadEntity(airHead, offset);
            airHeadEntity.spawnAirHead();
            entities.add(airHeadEntity);
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
}
