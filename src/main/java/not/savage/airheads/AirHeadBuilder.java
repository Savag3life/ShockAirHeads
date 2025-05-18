package not.savage.airheads;

import not.savage.airheads.config.AirHeadConfig;
import not.savage.airheads.hologram.BillboardConstraints;
import not.savage.airheads.hologram.TextAlignment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;

import java.util.List;
import java.util.Optional;

/**
 * Builder class for creating AirHeads.
 * Expected usage is to chain methods together to set the
 * various properties of the AirHead. Then interact with {@link AirHeadAPI}
 * to register & spawn the configured AirHead.
 */
public class AirHeadBuilder {

    /**
     * The AirHeadConfig object that is used to create the AirHead.
     * @see AirHeadConfig
     * @see not.savage.airheads.hologram.HologramConfig
     */
    protected final AirHeadConfig airHeadConfig;

    /**
     * Constructor for the AirHeadBuilder.
     * @param location The location of the AirHead.
     */
    private AirHeadBuilder(final Location location) {
        this.airHeadConfig = new AirHeadConfig(location);
    }

    public static AirHeadBuilder create(final Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return new AirHeadBuilder(location);
    }

    /**
     * Set the AirHead skin texture.
     * @param texture Base64 encoded texture string.
     * @return self
     */
    public AirHeadBuilder setTexture(final String texture) {
        if (texture == null || texture.isEmpty()) {
            throw new IllegalArgumentException("Texture cannot be null or empty");
        }
        airHeadConfig.getAppearanceSettings().setHeadTexture(texture);
        return this;
    }

    /**
     * Set the AirHeads scale size.
     * @param scale Scale size.
     * @return self
     */
    public AirHeadBuilder setScale(final float scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale must be greater than 0");
        }
        airHeadConfig.getAppearanceSettings().setScale(scale);
        return this;
    }

    /**
     * Set the AirHeads overlay/wrapper material.
     * @param overlayMaterial Material to use.
     * @return self
     */
    public AirHeadBuilder setOverlayMaterial(final Material overlayMaterial) {
        if (overlayMaterial == null) {
            throw new IllegalArgumentException("Overlay material cannot be null");
        }
        airHeadConfig.getAppearanceSettings().setOverlayMaterial(overlayMaterial);
        return this;
    }

    /**
     * Set the AirHeads overlay/wrapper offset. How much lower the overlay
     * entity spawns compared to the head entity.
     * @param offset Offset to use.
     * @return self
     */
    public AirHeadBuilder setOverlayOffset(final double offset) {
        airHeadConfig.getAppearanceSettings().setOverlayOffset(offset);
        return this;
    }

    /**
     * Commands executed by the player whenever the Airhead is
     * interacted with by a player. %player% in commands is replaced
     * with the interacting players name.
     * @param interactCommands Commands to execute.
     * @return self
     */
    public AirHeadBuilder setInteractCommands(final List<String> interactCommands) {
        if (interactCommands == null || interactCommands.isEmpty()) {
            throw new IllegalArgumentException("Interact commands cannot be null or empty");
        }
        airHeadConfig.getInteractSettings().setInteractCommands(interactCommands);
        return this;
    }

    /**
     * Commands executed only if the interaction is a left click.
     * %player% in commands is replaced with the interacting players name.
     * Commands executed as the player.
     * @return self
     */
    public AirHeadBuilder setLeftClickCommands(final List<String> leftClickCommands) {
        if (leftClickCommands == null) {
            throw new IllegalArgumentException("Left click commands cannot be null");
        }
        airHeadConfig.getInteractSettings().setLeftClickCommands(leftClickCommands);
        return this;
    }

    /**
     * Commands executed only if the interaction is a right click.
     * %player% in commands is replaced with the interacting players name.
     * Commands executed as the player.
     * @return self
     */
    public AirHeadBuilder setRightClickCommands(final List<String> rightClickCommands) {
        if (rightClickCommands == null) {
            throw new IllegalArgumentException("Right click commands cannot be null");
        }
        airHeadConfig.getInteractSettings().setRightClickCommands(rightClickCommands);
        return this;
    }

    /**
     * Commands executed by the console whenever the Airhead is
     * interacted with by a player. %player% in commands is replaced
     * with the interacting players name.
     * @param consoleCommands Commands to execute
     * @return self
     */
    public AirHeadBuilder setConsoleCommands(final List<String> consoleCommands) {
        if (consoleCommands == null || consoleCommands.isEmpty()) {
            throw new IllegalArgumentException("Console commands cannot be null or empty");
        }
        airHeadConfig.getInteractSettings().setConsoleCommands(consoleCommands);
        return this;
    }

    /**
     * Commands executed only if the interaction is a left click.
     * %player% in commands is replaced with the interacting players name.
     * Commands executed as the console.
     * @return self
     */
    public AirHeadBuilder setLeftClickConsoleCommands(final List<String> leftClickConsoleCommands) {
        if (leftClickConsoleCommands == null) {
            throw new IllegalArgumentException("Left click console commands cannot be null");
        }
        airHeadConfig.getInteractSettings().setLeftClickConsoleCommands(leftClickConsoleCommands);
        return this;
    }

    /**
     * Commands executed only if the interaction is a right click.
     * %player% in commands is replaced with the interacting players name.
     * Commands executed as the console.
     * @return self
     */
    public AirHeadBuilder setRightClickConsoleCommands(final List<String> rightClickConsoleCommands) {
        if (rightClickConsoleCommands == null) {
            throw new IllegalArgumentException("Right click console commands cannot be null");
        }
        airHeadConfig.getInteractSettings().setRightClickConsoleCommands(rightClickConsoleCommands);
        return this;
    }

    /**
     * Message to send when interacted with. Supports mini-message.
     * @param interactMessage Message to send.
     * @return self
     */
    public AirHeadBuilder setInteractMessage(final List<String> interactMessage) {
        if (interactMessage == null) {
            throw new IllegalArgumentException("Interact message cannot be null");
        }
        airHeadConfig.getInteractSettings().setInteractMessage(interactMessage);
        return this;
    }

    /**
     * Sound to be played when interacting.
     * @param sound Sound to play.
     * @return self
     */
    public AirHeadBuilder setSound(NamespacedKey sound, float volume, float pitch) {
        if (sound == null) {
            throw new IllegalArgumentException("Sound cannot be null");
        }
        airHeadConfig.getInteractSettings().getSoundSettings().setSound(sound);
        airHeadConfig.getInteractSettings().getSoundSettings().setVolume(volume);
        airHeadConfig.getInteractSettings().getSoundSettings().setPitch(pitch);
        return this;
    }

    /**
     * Should the floating up/down animation be enabled?
     * @param doFloat true to enable, false to disable.
     * @return self
     */
    public AirHeadBuilder floating(boolean doFloat) {
        airHeadConfig.getAnimationSettings().setDoFloat(doFloat);
        return this;
    }

    /**
     * How high the head floats up, Larger Number = Higher
     * @param floatUpMax How high the head floats up.
     * @return self
     */
    public AirHeadBuilder floatUpRange(double floatUpMax) {
        airHeadConfig.getAnimationSettings().setFloatUpMax(floatUpMax);
        return this;
    }

    /**
     * How high the head floats down, Larger Number = Higher
     * @param floatDownMax How high the head floats down.
     * @return self
     */
    public AirHeadBuilder floatDownRange(double floatDownMax) {
        airHeadConfig.getAnimationSettings().setFloatDownMax(floatDownMax);
        return this;
    }

    /**
     * How fast the head floats up & down, Larger Number = Slower
     * @param floatCycleDurationTicks How fast the head floats up & down.
     * @return self
     */
    public AirHeadBuilder floatCycleDuration(long floatCycleDurationTicks) {
        airHeadConfig.getAnimationSettings().setFloatCycleDurationTicks(floatCycleDurationTicks);
        return this;
    }

    /**
     * Should the head rotate?
     * @param doRotation true to enable, false to disable.
     * @return self
     */
    public AirHeadBuilder rotating(boolean doRotation) {
        airHeadConfig.getAnimationSettings().setDoRotation(doRotation);
        return this;
    }

    /**
     * How fast the head rotates, Smaller Number = Slower
     * @param rotationPerTick How fast the head rotates.
     * @return self
     */
    public AirHeadBuilder rotationSpeed(int rotationPerTick) {
        airHeadConfig.getAnimationSettings().setRotationPerTick(rotationPerTick);
        return this;
    }

    /**
     * The text displayed in the hologram of the
     * AirHead. Supports mini-message & PlaceholderAPI.
     * @param hologramText The text to display in the hologram.
     * @return self
     */
    public AirHeadBuilder hologramText(final List<String> hologramText) {
        if (hologramText == null) {
            throw new IllegalArgumentException("Hologram text cannot be null or empty");
        }
        airHeadConfig.getHologramTextDisplaySettings().setHologramText(hologramText);
        return this;
    }

    /**
     * The space between the top of the airhead & the hologram
     * @param hologramOffset The offset of the hologram above the head.
     * @return self
     */
    public AirHeadBuilder hologramOffset(double hologramOffset) {
        airHeadConfig.getHologramTextDisplaySettings().setHologramOffset(hologramOffset);
        return this;
    }

    /**
     * The scale of the hologram entity.
     * @param scaleX Scale X.
     * @param scaleY Scale Y.
     * @param scaleZ Scale Z.
     * @return self
     */
    public AirHeadBuilder hologramScale(float scaleX, float scaleY, float scaleZ) {
        airHeadConfig.getHologramTextDisplaySettings().setScaleX(scaleX);
        airHeadConfig.getHologramTextDisplaySettings().setScaleY(scaleY);
        airHeadConfig.getHologramTextDisplaySettings().setScaleZ(scaleZ);
        return this;
    }

    /**
     * The rotation of the hologram entity.
     * @param translationX Translation X.
     * @param translationY Translation Y.
     * @param translationZ Translation Z.
     * @return self
     */
    public AirHeadBuilder hologramRotation(float translationX, float translationY, float translationZ) {
        airHeadConfig.getHologramTextDisplaySettings().setTranslationX(translationX);
        airHeadConfig.getHologramTextDisplaySettings().setTranslationY(translationY);
        airHeadConfig.getHologramTextDisplaySettings().setTranslationZ(translationZ);
        return this;
    }

    /**
     * The override width of the hologram entity.
     * If text width is larger, it will be overridden.
     * @param width Width of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramWidth(int width) {
        airHeadConfig.getHologramTextDisplaySettings().setWidth(width);
        return this;
    }

    /**
     * The override height of the hologram entity.
     * If text height is larger, it will be overridden.
     * @param height Height of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramHeight(int height) {
        airHeadConfig.getHologramTextDisplaySettings().setHeight(height);
        return this;
    }

    /**
     * Should the hologram text have a shadow?
     * @param hasTextShadow true to enable, false to disable.
     * @return self
     */
    public AirHeadBuilder hologramTextShadow(boolean hasTextShadow) {
        airHeadConfig.getHologramTextDisplaySettings().setHasTextShadow(hasTextShadow);
        return this;
    }

    /**
     * Should the hologram background be transparent?
     * @param transparentBackground true to enable, false to disable.
     * @return self
     */
    public AirHeadBuilder hologramTransparentBackground(boolean transparentBackground) {
        airHeadConfig.getHologramTextDisplaySettings().setTransparentBackground(transparentBackground);
        return this;
    }

    /**
     * The background color of the hologram. If transparentBackground
     * is enabled, this will be ignored.
     * @param backgroundColor The background color of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramBackgroundColor(final String backgroundColor) {
        if (backgroundColor == null || backgroundColor.isEmpty()) {
            throw new IllegalArgumentException("Background color cannot be null or empty");
        }
        airHeadConfig.getHologramTextDisplaySettings().setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * The radius of the shadow of the hologram.
     * @param shadowRadius The radius of the shadow.
     * @return self
     */
    public AirHeadBuilder hologramShadowRadius(float shadowRadius) {
        airHeadConfig.getHologramTextDisplaySettings().setShadowRadius(shadowRadius);
        return this;
    }

    /**
     * The strength of the shadow of the hologram.
     * @param shadowStrength The strength of the shadow.
     * @return self
     */
    public AirHeadBuilder hologramShadowStrength(float shadowStrength) {
        airHeadConfig.getHologramTextDisplaySettings().setShadowStrength(shadowStrength);
        return this;
    }

    /**
     * How often the hologram is updated. 1 = every tick, 2 = every other tick, etc.
     * -1 to disable ticking. Should be -1 if no placeholders are used.
     * @param updateIntervalTicks The update interval of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramUpdateInterval(int updateIntervalTicks) {
        airHeadConfig.getHologramTextDisplaySettings().setUpdateIntervalTicks(updateIntervalTicks);
        return this;
    }

    /**
     * The distance at which the hologram is rendered.
     * @param renderDistance The render distance of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramRenderDistance(int renderDistance) {
        airHeadConfig.getHologramTextDisplaySettings().setRenderDistance(renderDistance);
        return this;
    }

    /**
     * The constraints of the hologram. FIXED, VERTICAL, HORIZONTAL, CENTERED
     * @see BillboardConstraints for Constraints options.
     * @param billboardConstraints The constraints of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramBillboardConstraints(final BillboardConstraints billboardConstraints) {
        airHeadConfig.getHologramTextDisplaySettings().setBillboardConstraints(billboardConstraints.name());
        return this;
    }

    /**
     * The constraints of the hologram. FIXED, VERTICAL, HORIZONTAL, CENTERED
     * @see BillboardConstraints for Constraints options.
     * @param billboardConstraints The constraints of the hologram.
     * @return self
     */
    public AirHeadBuilder hologramBillboardConstraints(final String billboardConstraints) {
        if (billboardConstraints == null || billboardConstraints.isEmpty()) {
            throw new IllegalArgumentException("Billboard constraints cannot be null or empty");
        }
        Optional<BillboardConstraints> constraints = BillboardConstraints.fromString(billboardConstraints);
        if (constraints.isEmpty()) {
            throw new IllegalArgumentException("Invalid billboard constraints: " + billboardConstraints);
        }
        airHeadConfig.getHologramTextDisplaySettings().setBillboardConstraints(constraints.get().name());
        return this;
    }

    /**
     * The alignment of the hologram text. CENTERED, LEFT, RIGHT
     * @see TextAlignment for Alignment options.
     * @param textAlignment The alignment of the hologram text.
     * @return self
     */
    public AirHeadBuilder hologramTextAlignment(final TextAlignment textAlignment) {
        airHeadConfig.getHologramTextDisplaySettings().setTextAlignment(textAlignment.name());
        return this;
    }

    /**
     * The alignment of the hologram text. CENTERED, LEFT, RIGHT
     * @see TextAlignment for Alignment options.
     * @param textAlignment The alignment of the hologram text.
     * @return self
     */
    public AirHeadBuilder hologramTextAlignment(final String textAlignment) {
        if (textAlignment == null || textAlignment.isEmpty()) {
            throw new IllegalArgumentException("Text alignment cannot be null or empty");
        }
        Optional<TextAlignment> alignment = TextAlignment.fromString(textAlignment);
        if (alignment.isEmpty()) {
            throw new IllegalArgumentException("Invalid text alignment: " + textAlignment);
        }
        airHeadConfig.getHologramTextDisplaySettings().setTextAlignment(alignment.get().name());
        return this;
    }

    /**
     * The pitch of the hologram text.
     * Used when the hologram is not fixed.
     * @param pitch The pitch of the hologram text.
     * @return self
     */
    public AirHeadBuilder hologramPitch(float pitch) {
        airHeadConfig.getHologramTextDisplaySettings().setPitch(pitch);
        return this;
    }

    /**
     * The yaw of the hologram text.
     * Used when the hologram is not fixed.
     * @param yaw The yaw of the hologram text.
     * @return self
     */
    public AirHeadBuilder hologramYaw(float yaw) {
        airHeadConfig.getHologramTextDisplaySettings().setYaw(yaw);
        return this;
    }
}
