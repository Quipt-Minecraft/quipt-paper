package me.quickscythe.quipt.utils.events;

import me.quickscythe.quipt.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Event {

    private final JSONObject data = new JSONObject();
    private Optional<Runnable> onEnd = Optional.empty();
    private Optional<ItemStack> reward = Optional.empty();
    private Optional<Advancement> task = Optional.empty();
    private long startTime = 0;

    public Event() {
    }

    public Event start() {
        startTime = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            track(player);
        }
        return this;
    }

    public long started() {
        return startTime;
    }

    public void end() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            finalize(player);
        }
        onEnd.ifPresent(Runnable::run);
        CoreUtils.logger().log("Event", "Event ended");
        File file = new File(CoreUtils.dataFolder(), "events/" + startTime + ".json");
        if (!file.getParentFile().exists())
            CoreUtils.logger().log("Event", "Creating Event directories: " + file.getParentFile().mkdirs());
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(data.toString(2));
            writer.close();
        } catch (Exception e) {
            CoreUtils.logger().error("Event", e);
        }

//        System.out.println(data.toString(2));
        List<String> keys = new ArrayList<>(data.keySet());
        keys.forEach(data::remove);
        startTime = 0;
    }

    public Event end(@NotNull Runnable onEnd) {
        this.onEnd = Optional.of(onEnd);
        return this;
    }

    public Event reward(@NotNull ItemStack reward) {
        this.reward = Optional.of(reward);
        return this;
    }

    public Optional<ItemStack> reward() {
        return reward;
    }

    public Event task(@NotNull Advancement task) {
        this.task = Optional.of(task);
        return this;
    }

    public Optional<Advancement> task() {
        return task;
    }


    public Event track(@NotNull Player player) {
        if (!data.has(String.valueOf(player.getUniqueId()))) {
            data.put(String.valueOf(player.getUniqueId()), new JSONObject());
        }
        for (Statistic stat : Statistic.values()) {
            if (stat.getType().equals(Statistic.Type.UNTYPED)) {
                data.getJSONObject(String.valueOf(player.getUniqueId())).put(stat.name() + "_start", player.getStatistic(stat));
                continue;
            }
            if (stat.getType().equals(Statistic.Type.ENTITY)) {
                for (EntityType type : EntityType.values()) {
                    if (type.equals(EntityType.UNKNOWN)) continue;
                    data.getJSONObject(String.valueOf(player.getUniqueId())).put(stat.name() + "_" + type.name() + "_start", player.getStatistic(stat, type));
                }
                continue;
            }
//            if (stat.getType().equals(Statistic.Type.BLOCK) || stat.getType().equals(Statistic.Type.ITEM)) {
//                for (Material material : Material.values()) {
//                    if (material.isBlock())
//                        data.getJSONObject(String.valueOf(player.getUniqueId())).put(stat.name() + "_" + material.name() + "_start", player.getStatistic(stat, material));
//                    if (material.isItem())
//                        data.getJSONObject(String.valueOf(player.getUniqueId())).put(stat.name() + "_" + material.name() + "_start", player.getStatistic(stat, material));
//                }
//            }
        }

        return this;
    }

    public JSONObject data(OfflinePlayer player) {
        return data.getJSONObject(String.valueOf(player.getUniqueId()));
    }

    public JSONObject data() {
        return data;
    }

    public void finalize(@NotNull OfflinePlayer player) {
        JSONObject playerData = data(player);
        playerData.put("NAME", player.getName());
        for (Statistic stat : Statistic.values()) {
            if (stat.getType() == Statistic.Type.UNTYPED) {
                int previous = playerData.has(stat.name()) ? playerData.getInt(stat.name()) : 0;
                int recent = player.getStatistic(stat) - (playerData.has(stat.name() + "_start") ? playerData.getInt(stat.name() + "_start") : 0);
                int total = previous + recent;
                playerData.remove(stat.name() + "_start");
                if (total > 0)
                    playerData.put(stat.name(), previous + recent);
            }
            if (stat.getType().equals(Statistic.Type.ENTITY)) {
                for (EntityType type : EntityType.values()) {
                    if (type.equals(EntityType.UNKNOWN)) continue;
                    int previous = playerData.has(stat.name() + "_" + type.name()) ? playerData.getInt(stat.name() + "_" + type.name()) : 0;
                    int recent = player.getStatistic(stat, type) - (playerData.has(stat.name() + "_" + type.name() + "_start") ? playerData.getInt(stat.name() + "_" + type.name() + "_start") : 0);
                    int total = previous + recent;
                    if (total > 0) {
                        if (!playerData.has(stat.name())) playerData.put(stat.name(), new JSONObject());
                        playerData.getJSONObject(stat.name()).put(type.name(), total);
                    }
                    playerData.remove(stat.name() + "_" + type.name() + "_start");
                }
            }
//            if (stat.getType().equals(Statistic.Type.BLOCK) || stat.getType().equals(Statistic.Type.ITEM)) {
//                for (Material material : Material.values()) {
//                    int previous = playerData.has(stat.name() + "_" + material.name()) ? playerData.getInt(stat.name() + "_" + material.name()) : 0;
//                    int recent = player.getStatistic(stat, material) - (playerData.has(stat.name() + "_" + material.name() + "_start") ? playerData.getInt(stat.name() + "_" + material.name() + "_start") : 0);
//                    int total = previous + recent;
//                    if (total == 0) continue;
//                    if (!playerData.has(stat.name())) playerData.put(stat.name(), new JSONObject());
//                    playerData.getJSONObject(stat.name()).put(material.name(), total);
//                    playerData.remove(stat.name() + "_" + material.name() + "_start");
//                }
//            }
        }
    }
}

