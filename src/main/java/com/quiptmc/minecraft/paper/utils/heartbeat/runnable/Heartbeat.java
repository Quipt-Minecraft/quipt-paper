package com.quiptmc.minecraft.paper.utils.heartbeat.runnable;

import com.quiptmc.minecraft.paper.utils.CoreUtils;
import com.quiptmc.minecraft.paper.utils.PaperIntegration;
import com.quiptmc.minecraft.paper.utils.heartbeat.Flutter;
import org.bukkit.Bukkit;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Heartbeat implements Runnable {

    PaperIntegration plugin;

    private final Map<Integer, FlutterTask> FLUTTERS = new HashMap<>();
    private final List<Integer> FLUTTERS_REMOVE = new ArrayList<>();
    final List<FlutterTask> FLUTTERS_ADD = new ArrayList<>();
    private int last_id = 0;

    public Heartbeat(PaperIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.plugin().isPresent()) {

            for(FlutterTask task : FLUTTERS_ADD)
                FLUTTERS.put(task.getId(), task);
            FLUTTERS_ADD.clear();
            for(int id : FLUTTERS_REMOVE)
                FLUTTERS.remove(id);
            FLUTTERS_REMOVE.clear();

            for(Map.Entry<Integer, FlutterTask> entry : FLUTTERS.entrySet()){
                try {
                    if(!entry.getValue().getFlutter().run()){
                        FLUTTERS_REMOVE.add(entry.getKey());
                        CoreUtils.integration().logger().log("Flutter "+entry.getKey(), "There was an error during this flutter. Removing from heartbeat.");
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }


            Bukkit.getScheduler().runTaskLater(plugin.plugin().get(), this, 5);
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
