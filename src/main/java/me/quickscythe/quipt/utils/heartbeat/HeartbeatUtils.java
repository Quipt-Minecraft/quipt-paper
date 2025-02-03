package me.quickscythe.quipt.utils.heartbeat;

import me.quickscythe.quipt.utils.PaperIntegration;
import me.quickscythe.quipt.utils.heartbeat.runnable.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class HeartbeatUtils {


    private static final Map<PaperIntegration, Heartbeat> heartbeats = new HashMap<>();


    public static BukkitTask init(PaperIntegration plugin) {
        if(!plugin.plugin().isPresent()) return null;
        Heartbeat heartbeat = new Heartbeat(plugin);
        heartbeats.put(plugin, heartbeat);
        return Bukkit.getScheduler().runTaskLater(plugin.plugin().get(), heartbeat, 30);
    }

    public static Heartbeat heartbeat(PaperIntegration plugin){
        return heartbeats.getOrDefault(plugin, null);
    }




}
