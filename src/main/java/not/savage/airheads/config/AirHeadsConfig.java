package not.savage.airheads.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main configuration file for AirHeads.
 * @see not.savage.airheads.utility.ConfigBuilder<>
 */
@ConfigSerializable @Getter
@Deprecated
@SuppressWarnings("FieldMayBeFinal")
public class AirHeadsConfig {

    private long floatAnimationOffsetTicks = 20;
    private List<AirHead> airHeads = new ArrayList<>(Arrays.asList(
            new AirHead()
    ));

}
