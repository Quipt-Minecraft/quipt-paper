package me.quickscythe.quipt.utils.heartbeat;

import me.quickscythe.quipt.api.registries.Registries;
import me.quickscythe.quipt.api.registries.Registry;
import me.quickscythe.quipt.utils.PaperIntegration;
import me.quickscythe.quipt.utils.heartbeat.runnable.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class HeartbeatUtils {

    private static final Registry<Heartbeat> registry = Registries.add("heartbeat", new Registry<>());

    public static BukkitTask init(PaperIntegration plugin) {
        if (!plugin.plugin().isPresent()) return null;
        Heartbeat heartbeat = new Heartbeat(plugin);
        registry.register(plugin.name(), heartbeat);
        return Bukkit.getScheduler().runTaskLater(plugin.plugin().get(), heartbeat, 30);
    }

    public static Heartbeat heartbeat(PaperIntegration plugin) {
        return registry.getOrDefault(plugin.name(), null);
    }
}
