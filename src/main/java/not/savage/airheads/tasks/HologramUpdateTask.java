package not.savage.airheads.tasks;

import not.savage.airheads.hologram.TextDisplayHologram;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramUpdateTask extends BukkitRunnable {

    private final TextDisplayHologram hologram;
    private final int updateInterval;
    private int taskId = -1;

    public HologramUpdateTask(TextDisplayHologram hologram, int updateInterval) {
        this.hologram = hologram;
        this.updateInterval = updateInterval;
    }

    @Override
    public void run() {
        hologram.getWorld().getPlayers().forEach(hologram::update);
    }

    public void register() {
        if (taskId != -1) {
            return;
        }
        taskId = runTaskTimerAsynchronously(hologram.getPlugin(), 0, updateInterval).getTaskId();
    }

    public void cancel() {
        if (taskId == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }
}
