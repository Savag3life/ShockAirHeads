package not.savage.airheads.tasks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import not.savage.airheads.AirHeadEntity;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

public class FloatAnimationTask extends BukkitRunnable {

    private final AirHeadEntity airHead;

    // Animation Details
    private final double minY;
    private final double maxY;
    private final double stepHeight; // min height
    private final int rotationSpeed;

    private final long initialDelay;

    private int rot = -180;
    private boolean goingUp = true;

    public FloatAnimationTask(Location origin, AirHeadEntity airHead, long initialDelay) {
        this.airHead = airHead;
        this.minY = (origin.getY() - this.airHead.getConfig().getFloatDownMax()); // min height
        this.maxY = (origin.getY() + this.airHead.getConfig().getFloatUpMax()); // max height
        double ticks = this.airHead.getConfig().getFloatCycleDurationTicks(); // Total ticks for a full float cycle
        this.stepHeight = (maxY - minY) / (ticks / 2); // Movement per tick
        this.rotationSpeed = this.airHead.getConfig().getRotationPerTick();
        this.initialDelay = initialDelay;
    }

    @Override
    public void run() {
        // Initial delay allows server owners to offset similarly timed animations
        // so that it isn't just a line of heads floating up and down at the same speed
        // and level. This is a simple way to add some variety.
        if (System.currentTimeMillis() < initialDelay) return;

        final Location curr = airHead.getCurrentLocation().clone();

        // Can configure an AirHead to not float.
        if (airHead.getConfig().isDoFloat()) {
            // Move the head up or down
            if (goingUp) {
                curr.add(0, stepHeight, 0);
                if (curr.getY() >= maxY) {
                    curr.setY(maxY); // Ensure it doesn't exceed the max height
                    goingUp = false; // Switch direction
                }
            } else {
                curr.subtract(0, stepHeight, 0);
                if (curr.getY() <= minY) {
                    curr.setY(minY); // Ensure it doesn't go below min height
                    goingUp = true; // Switch direction
                }
            }
        }

        // Can configure an AirHead to not rotate.
        if (airHead.getConfig().isDoRotation()) {
            // Apply rotation
            curr.setYaw(rot);
            rot += rotationSpeed;
            if (rot > 180) {
                rot = -180 + (rot - 180);
            }
        }

        airHead.teleport(curr);
    }
}
