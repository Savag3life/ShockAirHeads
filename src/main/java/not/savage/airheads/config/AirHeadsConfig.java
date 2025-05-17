package not.savage.airheads.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable @Data
@NoArgsConstructor
@SuppressWarnings("FieldMayBeFinal") // Configurate requires this to be mutable
public class AirHeadsConfig {

    private long floatAnimationOffsetTicks = 20;
    private HashMap<String, AirHeadConfig> airHeads = new HashMap<>();

}
