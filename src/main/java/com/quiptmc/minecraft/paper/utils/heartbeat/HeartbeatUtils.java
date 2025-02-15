package com.quiptmc.minecraft.paper.utils.heartbeat;

import com.quiptmc.core.data.registries.Registries;
import com.quiptmc.core.data.registries.Registry;
import com.quiptmc.minecraft.paper.utils.PaperIntegration;
import com.quiptmc.minecraft.paper.utils.heartbeat.runnable.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class HeartbeatUtils {

    private static final Registry<Heartbeat> registry = Registries.REGISTRAR.add(Registries.KEYS.register("heartbeat").get(), new Registry<>(Heartbeat.class));

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
