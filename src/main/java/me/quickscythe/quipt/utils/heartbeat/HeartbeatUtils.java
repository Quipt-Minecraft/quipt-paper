package me.quickscythe.quipt.utils.heartbeat;

import me.quickscythe.quipt.utils.heartbeat.runnable.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class HeartbeatUtils {


    private static final Map<JavaPlugin, Heartbeat> heartbeats = new HashMap<>();


    public static void init(JavaPlugin plugin) {
        Heartbeat heartbeat = new Heartbeat(plugin);
        heartbeats.put(plugin, heartbeat);
        Bukkit.getScheduler().runTaskLater(plugin, heartbeat, 30);
    }

    public static Heartbeat heartbeat(JavaPlugin plugin){
        return heartbeats.getOrDefault(plugin, null);
    }




}
