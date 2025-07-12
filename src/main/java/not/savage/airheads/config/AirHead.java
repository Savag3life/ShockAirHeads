package not.savage.airheads.config;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.*;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * The config settings for an individual AirHead.
 */
@ConfigSerializable @Getter @Setter
@NoArgsConstructor
@SuppressWarnings("FieldMayBeFinal")
public class AirHead {

    public AirHead(Location location) {
        this.location = location;
    }

    @Comment("The location this head will be placed at. The location is corrected to the block center.")
    private Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);

    @Comment("The styling and text of the hologram above the head. Supports MiniMessage <> Colors only.")
    private List<String> hologramText = List.of(
            "<gradient:#a403ff:#d5b7fd><bold>Default Hologram",
            "<gray>supremeventures.ca/discord"
    );

    @Comment("The offset of the hologram above the head. Larger Number = Higher")
    private double hologramOffset = 1.3;

    @Comment("The head texture, in base64 format. You can use https://minecraft-heads.com/ to get a texture.")
    private String headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM5ZWU3MTU0OTc5YjNmODc3MzVhMWM4YWMwODc4MTRiNzkyOGQwNTc2YTI2OTViYTAxZWQ2MTYzMTk0MjA0NSJ9fX0=";

    @Comment("Commands to execute when head is interacted with. Executed as the integrating player.")
    private List<String> interactCommands = List.of("discord");

    @Comment("Commands which are only executed if the interaction is a left click.")
    private List<String> leftClickCommands = new ArrayList<>();

    @Comment("Commands which are only executed if the interaction is a right click.")
    private List<String> rightClickCommands = new ArrayList<>();

    @Comment("Commands to execute when head is interacted with. Executed as console sender.")
    private List<String> consoleCommands = new ArrayList<>();

    @Comment("Commands which are only executed if the interaction is a left click. Executed as console sender.")
    private List<String> leftClickConsoleCommands = new ArrayList<>();

    @Comment("Commands which are only executed if the interaction is a right click. Executed as console sender.")
    private List<String> rightClickConsoleCommands = new ArrayList<>();

    @Comment("Message to send when interacted with. Supports mini-message.")
    private List<String> interactMessage = new ArrayList<>();

    @Comment("Sound to be played when interacting")
    @Getter private SoundSettings soundSettings = new SoundSettings();
    @ConfigSerializable @Setter @Getter public static class SoundSettings {
         private float volume = 1.0f;
         private float pitch = 1.0f;
         private Sound sound = RegistryAccess.registryAccess()
                 .getRegistry(RegistryKey.SOUND_EVENT)
                 .get(new NamespacedKey("minecraft", "entity.player.levelup"));
         private boolean enabled = true;
    }

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

    @Comment("Change the scale of the airhead entity. Larger Number = Bigger")
    private double scale = 1.0;

    @Comment("The material to overlay the head with, like glass or ice. AIR = No Overlay")
    private Material overlayMaterial = Material.AIR;

    @Comment("The scale offset of the overlay material. Larger number = larger overlay")
    private double overlayOffset = 0.45;
}
