package not.savage.airheads.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

/**
 * The config settings for an individual AirHead.
 * @see not.savage.airheads.config.AirHeadsConfig
 */
@ConfigSerializable @Getter
@SuppressWarnings("FieldMayBeFinal")
public class AirHead {

    @Comment("The location this head will be placed at. The location is corrected to the block center.")
    private Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);

    @Comment("The styling and text of the hologram above the head. Supports MiniMessage <> Colors only.")
    private List<String> hologramText = List.of(
            "<gradient:#a403ff:#d5b7fd><bold>Server Discord",
            "<gray>supremeventures.ca/discord"
    );

    @Comment("The head texture, in base64 format. You can use https://minecraft-heads.com/ to get a texture.")
    private String headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM5ZWU3MTU0OTc5YjNmODc3MzVhMWM4YWMwODc4MTRiNzkyOGQwNTc2YTI2OTViYTAxZWQ2MTYzMTk0MjA0NSJ9fX0=";

    @Comment("Commands to execute when head is interacted with. Executed as the integrating player.")
    private List<String> interactCommands = List.of("discord");

    @Comment("Should the head do a floating animation going up and down?")
    private boolean doFloat = true;

    @Comment("How high the head floats up, Larger Number = Higher")
    private double floatUpMax = 0.5;

    @Comment("How high the head floats down, Larger Number = Higher")
    private double floatDownMax = 0.5;

    @Comment("How fast the head floats up & down, Larger Number = Slower")
    private long floatCycleDurationTicks = 80;

    @Comment("Should the head rotate?")
    private boolean doRotation = true;

    @Comment("How fast the head rotates, Smaller Number = Slower")
    private int rotationPerTick = 5;

}
