package not.savage.airheads.hologram;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable @Data
@SuppressWarnings("FieldMayBeFinal")
@Builder(access = AccessLevel.PUBLIC)
public class HologramConfig {

    // How the Hologram is displayed. FIXED, VERTICAL, HORIZONTAL, CENTERED
    private String billboardConstraints = BillboardConstraints.VERTICAL.name();
    private String textAlignment = TextAlignment.CENTERED.name();

    // The text to display, MiniMessage format.
    private List<String> hologramText = List.of(
            "<gradient:#a403ff:#d5b7fd><bold>Default Hologram</bold>",
            "<gray>supremeventures.ca/discord"
    );

    private float pitch = 0.0F;
    private float yaw = 0.0F;

    // The space between the top of the airhead & the hologram
    private double hologramOffset = 0.5;

    // Used to scale the hologram entity
    private float scaleX = 1.0F;
    private float scaleY = 1.0F;
    private float scaleZ = 1.0F;

    // The rotation of the hologram entity
    private float translationX = 0.0F;
    private float translationY = 0.0F;
    private float translationZ = 0.0F;

    private int width = 200;
    private int height = 50;

    private boolean hasTextShadow = false;
    private boolean transparentBackground = true;
    private String backgroundColor = "#40000000";

    private float shadowRadius = 0.0F;
    private float shadowStrength = 0.0F;

    // How often the hologram is updated. 1 = every tick, 2 = every other tick, etc. -1 to disable ticking.
    private int updateIntervalTicks = -1;
    private int renderDistance = 100;
}
