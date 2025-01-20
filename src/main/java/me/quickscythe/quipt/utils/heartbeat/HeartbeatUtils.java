package me.quickscythe.quipt.utils.heartbeat;

import me.quickscythe.quipt.utils.heartbeat.runnable.Heartbeat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HeartbeatUtils {

    private static Heartbeat heartbeat;


    public static void init(JavaPlugin plugin) {
        heartbeat = new Heartbeat(plugin);
        Bukkit.getScheduler().runTaskLater(plugin, heartbeat, 30);
    }

    public static Heartbeat heartbeat(){
        return heartbeat;
    }




}
