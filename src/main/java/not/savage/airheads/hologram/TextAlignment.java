package not.savage.airheads.hologram;

import java.util.Optional;

public enum TextAlignment {

    CENTERED(0),
    LEFT(1),
    RIGHT(2);

    private final int id;

    TextAlignment(int id) {
        this.id = id;
    }

    public static Optional<TextAlignment> fromString(String name) {
        for (TextAlignment alignment : values()) {
            if (alignment.name().equalsIgnoreCase(name)) {
                return Optional.of(alignment);
            }
        }
        return Optional.empty();
    }

    /**
     * Pack the alignment into the hologram's metadata byte.
     * @param hologram The hologram to align.
     */
    public void align(final TextDisplayHologram hologram) {
        byte meta = hologram.getMeta();
        meta &= ~0x18;
        meta |= (byte) (this.id << 3);
        hologram.setMeta(meta);
    }
}
