package not.savage.airheads.tasks;

import not.savage.airheads.AirHeadEntity;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class FloatAnimationTask extends BukkitRunnable {

    private final AirHeadEntity airHead;

    // Animation Details
    private final double minY;
    private final double maxY;
    private final double stepHeight; // min height
    private final int rotationSpeed;

    private final boolean rotate;
    private final boolean floating;

    private final long initialDelay;

    private int rot = -180;
    private boolean goingUp = true;

    public FloatAnimationTask(Location origin, AirHeadEntity airHead, long initialDelay) {
        this.airHead = airHead;
        this.minY = (origin.getY() - this.airHead.getConfig().getAnimationSettings().getFloatDownMax()); // min height
        this.maxY = (origin.getY() + this.airHead.getConfig().getAnimationSettings().getFloatUpMax()); // max height
        double ticks = this.airHead.getConfig().getAnimationSettings().getFloatCycleDurationTicks(); // Total ticks for a full float cycle
        this.stepHeight = (maxY - minY) / (ticks / 2); // Movement per tick
        this.rotationSpeed = this.airHead.getConfig().getAnimationSettings().getRotationPerTick();
        this.initialDelay = initialDelay;
        this.rotate = this.airHead.getConfig().getAnimationSettings().isDoRotation();
        this.floating = this.airHead.getConfig().getAnimationSettings().isDoFloat();
    }

    @Override
    public void run() {
        // Initial delay allows server owners to offset similarly timed animations
        // so that it isn't just a line of heads floating up and down at the same speed
        // and level. This is a simple way to add some variety.
        if (System.currentTimeMillis() < initialDelay) return;

        final Location curr = airHead.getCurrentLocation().clone();

        // Can configure an AirHead to not float.
        if (floating) {
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
        if (rotate) {
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
