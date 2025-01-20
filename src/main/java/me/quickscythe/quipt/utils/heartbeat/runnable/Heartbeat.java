package me.quickscythe.quipt.utils.heartbeat.runnable;

import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.chat.Logger;
import me.quickscythe.quipt.utils.heartbeat.Flutter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Heartbeat implements Runnable {

    JavaPlugin plugin;

    private final Map<Integer, FlutterTask> FLUTTERS = new HashMap<>();
    private final List<Integer> FLUTTERS_REMOVE = new ArrayList<>();
    final List<FlutterTask> FLUTTERS_ADD = new ArrayList<>();
    private int last_id = 0;

    public Heartbeat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.isEnabled()) {

            for(FlutterTask task : FLUTTERS_ADD)
                FLUTTERS.put(task.getId(), task);
            FLUTTERS_ADD.clear();
            for(int id : FLUTTERS_REMOVE)
                FLUTTERS.remove(id);
            FLUTTERS_REMOVE.clear();

            for(Map.Entry<Integer, FlutterTask> entry : FLUTTERS.entrySet()){
                if(!entry.getValue().getFlutter().run()){
                    FLUTTERS_REMOVE.add(entry.getKey());
                    CoreUtils.logger().log(Logger.LogLevel.ERROR, "Flutter "+entry.getKey(), "There was an error during this flutter. Removing from heartbeat.");
                }
            }


            Bukkit.getScheduler().runTaskLater(plugin, this, 5);
        }
    }

    public FlutterTask addFlutter(Flutter flutter){
        last_id = last_id+1;
        FlutterTask task = new FlutterTask(last_id, flutter);
        FLUTTERS_ADD.add(task);
        return task;
    }

    public void removeFlutter(FlutterTask task){
        removeFlutter(task.getId());
    }

    public void removeFlutter(int id){
        FLUTTERS_REMOVE.add(id);
    }

    public Iterable<? extends FlutterTask> getAddQueue() {
        return FLUTTERS_ADD;
    }

    public static class FlutterTask {

        private final int id;
        private final Flutter flutter;

        protected FlutterTask(int id, Flutter flutter) {
            this.id = id;
            this.flutter = flutter;
        }

        public int getId(){
            return id;
        }

        public Flutter getFlutter(){
            return flutter;
        }
    }
}
