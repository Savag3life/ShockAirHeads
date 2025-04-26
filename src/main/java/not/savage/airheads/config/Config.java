package not.savage.airheads.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal") // Configurate requires this to be mutable
public class Config {
    private long floatAnimationOffsetTicks = 20;
    private HashMap<String, AirHead> airHeads = new HashMap<>();
}
