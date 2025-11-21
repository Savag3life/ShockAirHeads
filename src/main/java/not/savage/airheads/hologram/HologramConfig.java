package not.savage.airheads.hologram;

import lombok.*;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable @Data
@SuppressWarnings("FieldMayBeFinal")
@Builder(toBuilder = true)
@AllArgsConstructor
public class HologramConfig {

    public HologramConfig() { /* Configurate No-Args Constructor */ }

    // How the Hologram is displayed. FIXED, VERTICAL, HORIZONTAL, CENTERED
    @Builder.Default private String billboardConstraints = BillboardConstraints.VERTICAL.name();
    @Builder.Default private String textAlignment = TextAlignment.CENTERED.name();

    // The text to display, MiniMessage format.
    @Builder.Default private List<String> hologramText = List.of(
            "<gradient:#a403ff:#d5b7fd><bold>Default Hologram</bold>",
            "<gray>supremeventures.ca/discord"
    );

    @Builder.Default private float pitch = 0.0F;
    @Builder.Default private float yaw = 0.0F;

    // The space between the top of the airhead & the hologram
    @Builder.Default private double hologramOffset = 0.5;

    // Used to scale the hologram entity
    @Builder.Default private float scaleX = 1.0F;
    @Builder.Default private float scaleY = 1.0F;
    @Builder.Default private float scaleZ = 1.0F;

    // The rotation of the hologram entity
    @Builder.Default private float translationX = 0.0F;
    @Builder.Default private float translationY = 0.0F;
    @Builder.Default private float translationZ = 0.0F;

    @Builder.Default private int width = 200;
    @Builder.Default private int height = 50;

    @Builder.Default private boolean hasTextShadow = false;
    @Builder.Default private boolean transparentBackground = true;
    @Builder.Default private String backgroundColor = "#40000000";

    @Builder.Default private float shadowRadius = 0.0F;
    @Builder.Default private float shadowStrength = 0.0F;

    // How often the hologram is updated. 1 = every tick, 2 = every other tick, etc. -1 to disable ticking.
    @Builder.Default private int updateIntervalTicks = -1;
    @Builder.Default private int renderDistance = 100;
}
