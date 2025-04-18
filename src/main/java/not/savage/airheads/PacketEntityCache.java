package not.savage.airheads;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PacketEntityCache {

    private final AirHeadsPlugin plugin;
    private final HashMap<Integer, AirHeadEntity> entityCache = new HashMap<>();

    public PacketEntityCache(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    public void addEntity(int entityId, AirHeadEntity entity) {
        entityCache.put(entityId, entity);
    }

    public AirHeadEntity getEntityByEntityId(int entityId) {
        return entityCache.get(entityId);
    }

    public AirHeadEntity getEntityByName(String name) {
        for (AirHeadEntity entity : entityCache.values()) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

    public void clear() {
        for (AirHeadEntity entity : entityCache.values()) {
            entity.remove();
        }
        entityCache.clear();
    }

    public void showWorld(Player player) {
        for (AirHeadEntity entity : entityCache.values()) {
            if (entity.getCurrentLocation().getWorld().getName().equals(player.getWorld().getName())) {
                entity.spawnForPlayer(player);
            }
        }
    }
}
