package not.savage.airheads.config;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import not.savage.airheads.hologram.HologramConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable @Data
@NoArgsConstructor
@SuppressWarnings("FieldMayBeFinal") // Configurate requires this to be mutable
public class AirHeadConfig {

    public AirHeadConfig(final Location location) {
        this.location = location;
    }

    private Location location;
    private HologramConfig hologramTextDisplaySettings = HologramConfig.builder().build();

    private AppearanceSettings appearanceSettings = new AppearanceSettings();
    @ConfigSerializable @Data public static class AppearanceSettings {
        @Comment("The head texture, in base64 format. You can use https://minecraft-heads.com/ to get a texture.")
        private String headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY5ZTRhNDNkODJmNWZlMWUxOTM4N2IzOGJmYzE3MDFhMTZiNzA0ZjA5Njg5OWU1MGRiY2JkNGUwNjkxNjg5YSJ9fX0=";

        @Comment("Change the scale of the airhead entity. Larger Number = Bigger")
        private double scale = 1.0;

        @Comment("The material to overlay the head with, like glass or ice. AIR = No Overlay")
        private Material overlayMaterial = Material.LIGHT_BLUE_STAINED_GLASS;

        @Comment("The scale offset of the overlay material. Larger number = larger overlay")
        private double overlayOffset = 0.45;
    }

    private InteractSettings interactSettings = new InteractSettings();
    @ConfigSerializable @Data public static class InteractSettings {

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
        @ConfigSerializable @Data public static class SoundSettings {
            private float volume = 1.0f;
            private float pitch = 1.0f;
            private NamespacedKey sound = NamespacedKey.minecraft("entity.player.levelup");
            private boolean enabled = true;

            private transient Sound cachedSound = null;
            public Sound sound() {
                if (cachedSound == null && sound != null) {
                    try {
                        cachedSound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(sound);
                    } catch (IllegalArgumentException ignored) {
                        // Silently fail if the sound is invalid since we don't have a logger here
                    }
                }
                return cachedSound;
            }
        }


        @Comment("Send to a bungee server within the network on interact")
        @Getter private String sendTo = "";
    }

    private AnimationSettings animationSettings = new AnimationSettings();
    @ConfigSerializable @Data public static class AnimationSettings {
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
}
