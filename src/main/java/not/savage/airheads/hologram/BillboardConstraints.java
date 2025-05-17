package not.savage.airheads.hologram;

import lombok.Getter;

import java.util.Optional;

public enum BillboardConstraints {

    // NO Rotation
    FIXED((byte) 0),
    // Rotation on Y Axis only
    VERTICAL((byte) 1),
    // Rotation on X Axis only
    HORIZONTAL((byte) 2),
    // Text is always facing the player (rotation on all axes)
    CENTERED((byte) 3);

    @Getter private final byte id;

    BillboardConstraints(byte id) {
        this.id = id;
    }

    public static Optional<BillboardConstraints> fromString(String name) {
        for (BillboardConstraints constraint : values()) {
            if (constraint.name().equalsIgnoreCase(name)) {
                return Optional.of(constraint);
            }
        }
        return Optional.empty();
    }
}
