package not.savage.airheads;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class AttributeUtility {

    private static Field SCALE_ATTRIBUTE;
    private static Field INTERACTION_RANGE_ATTRIBUTE;

    static {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AttributeUtility.class);
        String[] gameVersion = Bukkit.getServer().getMinecraftVersion().split("\\.");
        int majorVersion = Integer.parseInt(gameVersion[1]);
        int minorVersion = Integer.parseInt(gameVersion[2]);

        if (majorVersion <= 20) {
            // 1.20.5 and below
            if (majorVersion < 20 && minorVersion < 5) {
                SCALE_ATTRIBUTE = null;
                INTERACTION_RANGE_ATTRIBUTE = null;
            } else if (majorVersion == 20 && (minorVersion == 5 || minorVersion == 6)) { // 1.20.5 & 1.20.6
                try {
                    SCALE_ATTRIBUTE = Attribute.class.getDeclaredField("GENERIC_SCALE");
                    INTERACTION_RANGE_ATTRIBUTE = Attribute.class.getDeclaredField("PLAYER_ENTITY_INTERACTION_RANGE");
                    plugin.getLogger().info("Using GENERIC_SCALE for 1.20.5");
                } catch (NoSuchFieldException e) {
                    plugin.getLogger().warning("Failed to resolve SCALE attribute field for " + Bukkit.getServer().getMinecraftVersion() + ". Tried to use GENERIC_SCALE.");
                }
            }
        } else { // >= 1.21
            if (majorVersion == 21 && minorVersion <= 2) { // > 1.20.2 - Paper Renames GENERIC_SCALE -> SCALE
                try {
                    SCALE_ATTRIBUTE = Attribute.class.getDeclaredField("GENERIC_SCALE");
                    INTERACTION_RANGE_ATTRIBUTE = Attribute.class.getDeclaredField("PLAYER_ENTITY_INTERACTION_RANGE");
                    plugin.getLogger().info("Using GENERIC_SCALE for <1.21.2");
                } catch (NoSuchFieldException e) {
                    plugin.getLogger().warning("Failed to resolve SCALE attribute field for " + Bukkit.getServer().getMinecraftVersion() + ". Tried to use GENERIC_SCALE.");
                }
            } else {
                try {
                    SCALE_ATTRIBUTE = Attribute.class.getDeclaredField("SCALE");
                    INTERACTION_RANGE_ATTRIBUTE = Attribute.class.getDeclaredField("ENTITY_INTERACTION_RANGE");
                    plugin.getLogger().info("Using SCALE for >=1.21.2");
                } catch (NoSuchFieldException e) {
                    plugin.getLogger().warning("Failed to resolve SCALE attribute field for " + Bukkit.getServer().getMinecraftVersion() + ". Tried to use SCALE.");
                }
            }
        }
    }

    public static Attribute getScaleAttribute() {
        if (SCALE_ATTRIBUTE == null) {
            return null;
        }
        try {
            return (Attribute) SCALE_ATTRIBUTE.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access scale attribute", e);
        }
    }

    public static Attribute getInteractionRangeAttribute() {
        if (INTERACTION_RANGE_ATTRIBUTE == null) {
            return null;
        }
        try {
            return (Attribute) INTERACTION_RANGE_ATTRIBUTE.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access interaction range attribute", e);
        }
    }
}
