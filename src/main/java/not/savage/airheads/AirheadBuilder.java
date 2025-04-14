package not.savage.airheads;

import not.savage.airheads.config.AirHead;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Developer API for creating AirHeads easily via a single Builder.
 * This is considered "Public" API, and will not change unless absolutely necessary.
 *
 * <p>Example usage:</p>
 * <pre>
 *     final AirHeadEntity entity = new AirheadBuilder()
 *          .setLocation(location)
 *          .setHologramText("Hello World")
 *          .setHeadTexture("texture")
 *          .setPlayerInteractCommands(List.of("command1", "command2"))
 *          .setPlayerInteractMessage(List.of("message1", "message2"))
 *          .setDoFloat(true)
 *          .setFloatUpMax(0.5)
 *          .setFloatDownMax(0.5)
 *          .setFloatCycleDurationTicks(20)
 *          .setDoRotation(true)
 *          .setRotationPerTick(1)
 *          .spawn();
 *
 * All fields are populated with a default instance of {@link AirHead}
 */
public class AirheadBuilder {

    private final AirHead airHead;

    /**
     * Creates a new AirheadBuilder instance.
     * This is the main entry point for creating AirHeads.
     */
    public AirheadBuilder() {
        this.airHead = new AirHead();
    }

    /**
     * Sets the location of the AirHead entity. This is the center/origin point of the animation.
     * @param location The location to spawn the AirHead at.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setLocation(final Location location) {
        this.airHead.setLocation(location);
        return this;
    }

    /**
     * Sets the hologram text for the AirHead entity.
     * @param hologramText The text to display on the hologram. Supports MiniMessage.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setHologramText(final List<String> hologramText) {
        airHead.setHologramText(hologramText);
        return this;
    }

    /**
     * Sets the hologram text for the AirHead entity.
     * @param hologramText The text to display on the hologram. Supports MiniMessage.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setHologramText(final String... hologramText) {
        airHead.setHologramText(List.of(hologramText));
        return this;
    }

    /**
     * Sets the offset of the hologram above the AirHead entity.
     * @param hologramOffset The offset of the hologram. Larger number = higher. negative number = below the head.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setHologramOffset(final double hologramOffset) {
        airHead.setHologramOffset(hologramOffset);
        return this;
    }

    /**
     * Sets the head texture for the AirHead entity.
     * @param texture The base64 texture string. You can use https://minecraft-heads.com/ to get a texture.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setHeadTexture(final String texture) {
        airHead.setHeadTexture(texture);
        return this;
    }

    /**
     * Sets the commands to be executed when the AirHead is interacted with.
     * @param commands The list of commands to execute. These will be executed as the player.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setPlayerInteractCommands(final List<String> commands) {
        airHead.setInteractCommands(commands);
        return this;
    }

    /**
     * Sets the console commands to be executed when the AirHead is interacted with.
     * @param commands The list of console commands to execute.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setInteractConsoleCommand(final List<String> commands) {
        airHead.setConsoleCommands(commands);
        return this;
    }

    /**
     * Sets the message to be sent to the player when they interact with the AirHead.
     * @param message The message to be sent to the player. Supports MiniMessage.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setPlayerInteractMessage(final List<String> message) {
        airHead.setInteractMessage(message);
        return this;
    }

    /**
     * Sets the sound settings for the AirHead entity.
     * @param sound The sound to be played when the AirHead is interacted with.
     * @param volume The volume of the sound. Default is 1.0.
     * @param pitch The pitch of the sound. Default is 1.0.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder playSound(Sound sound, float volume, float pitch) {
        airHead.getSoundSettings().setSound(sound);
        airHead.getSoundSettings().setVolume(volume);
        airHead.getSoundSettings().setPitch(pitch);
        airHead.getSoundSettings().setEnabled(true);
        return this;
    }

    /**
     * Disables the default sound settings for the AirHead entity.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder noSound() {
        airHead.getSoundSettings().setEnabled(false);
        return this;
    }

    /**
     * Sets whether the AirHead entity should float.
     * @param doFloat True if the AirHead should float, false otherwise.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setDoFloat(final boolean doFloat) {
        airHead.setDoFloat(doFloat);
        return this;
    }

    /**
     * Sets the maximum height the AirHead entity can float up.
     * @param floatUpMax The maximum height the AirHead can float up.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setFloatUpMax(final double floatUpMax) {
        airHead.setFloatUpMax(floatUpMax);
        return this;
    }

    /**
     * Sets the maximum height the AirHead entity can float down.
     * @param floatDownMax The maximum height the AirHead can float down.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setFloatDownMax(final double floatDownMax) {
        airHead.setFloatDownMax(floatDownMax);
        return this;
    }

    /**
     * Sets the duration of the float cycle in ticks.
     * @param floatCycleDurationTicks The duration of the float cycle in ticks.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setFloatCycleDurationTicks(final long floatCycleDurationTicks) {
        airHead.setFloatCycleDurationTicks(floatCycleDurationTicks);
        return this;
    }

    /**
     * Sets whether the AirHead entity should rotate.
     * @param doRotation True if the AirHead should rotate, false otherwise.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setDoRotation(final boolean doRotation) {
        airHead.setDoRotation(doRotation);
        return this;
    }

    /**
     * Sets the rotation speed of the AirHead entity.
     * @param rotationPerTick The number of degrees to rotate per tick. Larger number = faster.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setRotationPerTick(final int rotationPerTick) {
        airHead.setRotationPerTick(rotationPerTick);
        return this;
    }

    /**
     * Sets the scale of the AirHead entity.
     * @param scale The scale of the AirHead entity. Larger number = bigger. Between 0.05 and 10.
     * @return The current instance of the builder for chaining.
     */
    public AirheadBuilder setScale(final double scale) {
        airHead.setScale(scale);
        return this;
    }

    /**
     * Spawns the AirHead entity at the location specified in the builder.
     * @return The spawned AirHeadEntity, or null if the plugin is not loaded.
     */
    public AirHeadEntity spawn() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AirheadBuilder.class);
        if (plugin instanceof AirHeadsPlugin airheads) {
            AirHeadEntity entity = new AirHeadEntity(airheads, airHead, 0);
            airheads.getPacketEntityCache().addEntity(entity.getEntityId(), entity);
            return entity;
        }
        return null;
    }
}
