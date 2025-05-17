package not.savage.airheads;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public interface AirHeadAPI {

    /**
     * Register a new AirHead. The unique ID of the AirHead is generated automatically
     * in format "{@link JavaPlugin#getName()}::{@link java.util.UUID#randomUUID()}".
     * Random ID is meant for temporary AirHeads that are not persistent.
     * @see #spawnAirHead(JavaPlugin, String, AirHeadBuilder, boolean)
     * @param plugin Owning plugin
     * @param builder AirHeadBuilder to build the AirHead
     * @return The newly created AirHeadEntity
     */
    default AirHeadEntity spawnAirHead(final JavaPlugin plugin, final AirHeadBuilder builder) {
        return spawnAirHead(plugin, plugin.getName() + "::" + UUID.randomUUID().toString(), builder, false);
    }

    /**
     * Unregister an AirHead from being managed.
     * @param plugin Owning plugin
     * @param customId The unique ID of the AirHead. Should be globally unique.
     * @param builder AirHeadBuilder to build the AirHead
     * @return The newly created AirHeadEntity
     */
    AirHeadEntity spawnAirHead(final JavaPlugin plugin, final String customId, final AirHeadBuilder builder, final boolean persistent);

    /**
     * Get an AirHeadEntity by its unique ID.
     * @param airHeadId The unique ID of the AirHead. Should be globally unique.
     * @return An Optional containing the AirHeadEntity if it exists, or empty if it does not.
     */
    Optional<AirHeadEntity> getAirHeadEntity(@NonNull final String airHeadId);

    /**
     * Remove an AirHeadEntity.
     * @param airHeadId The unique ID of the AirHead.
     * @return boolean success of removal
     */
    boolean removeAirHead(@NonNull final String airHeadId);


}
