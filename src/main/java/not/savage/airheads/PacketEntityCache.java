package not.savage.airheads;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PacketEntityCache {

    private final AirHeadsPlugin plugin;
    private final HashMap<Integer, AirHeadEntity> byEntityId = new HashMap<>();
    private final HashMap<Integer, AirHeadEntity> byGlassOverlayId = new HashMap<>();

    public PacketEntityCache(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    public void addEntity(int entityId, AirHeadEntity entity) {
        byEntityId.put(entityId, entity);
        if (entity.getGlassItem() != null) {
            byGlassOverlayId.put(entity.getGlassEntityId(), entity);
        }
    }

    public AirHeadEntity getEntityByEntityId(int entityId) {
        AirHeadEntity entity = byEntityId.get(entityId);
        if (entity == null) {
            entity = byGlassOverlayId.get(entityId);
        }
        return entity;
    }

    public AirHeadEntity getEntityByName(String name) {
        for (AirHeadEntity entity : byEntityId.values()) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

    public AirHeadEntity getByAirHeadName(String name) {
        for (AirHeadEntity entity : byEntityId.values()) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    public void clear() {
        for (AirHeadEntity entity : byEntityId.values()) {
            entity.remove();
        }
        byEntityId.clear();
    }

    public void removeEntity(int entityId) {
        AirHeadEntity entity = byEntityId.remove(entityId);
        if (entity != null) {
            entity.remove();
        }
    }

    public void removeEntityByName(String name) {
        AirHeadEntity entity = getEntityByName(name);
        if (entity != null) {
            entity.remove();
        }
    }

    public void showWorld(Player player) {
        for (AirHeadEntity entity : byEntityId.values()) {
            if (entity.getCurrentLocation().getWorld().getName().equals(player.getWorld().getName())) {
                entity.spawnForPlayer(player);
            }
        }
    }
}
