package me.quickscythe.quipt.utils.sessions;

import me.quickscythe.quipt.api.QuiptPlugin;
import me.quickscythe.quipt.api.config.Config;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.SessionConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<UUID, JSONObject> CURRENT_SESSIONS = new HashMap<>();
    private static SessionConfig config;

    public static void start(QuiptPlugin plugin) {
        config = ConfigManager.registerConfig(plugin, SessionConfig.class);
    }

    public static JSONObject getSession(Player player) {
        return CURRENT_SESSIONS.getOrDefault(player.getUniqueId(), null);
    }

    public static Config getConfig() {
        return config;
    }

    public static void finish() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            finishSession(player);
        }
        config.save();
    }

    public static void startSession(Player player) {
        JSONObject session = new JSONObject();
        session.put("JOINED", System.currentTimeMillis());
        for (Statistic stat : Statistic.values()) {
            if (stat.getType().equals(Statistic.Type.UNTYPED)) {
                session.put(stat.name() + "_start", player.getStatistic(stat));
                continue;
            }
            if (stat.getType().equals(Statistic.Type.ENTITY)) {
                for (EntityType type : EntityType.values()) {
                    if (type.equals(EntityType.UNKNOWN)) continue;
                    session.put(stat.name() + "_" + type.name() + "_start", player.getStatistic(stat, type));
                }
                continue;
            }
            if (stat.getType().equals(Statistic.Type.BLOCK) || stat.getType().equals(Statistic.Type.ITEM)) {
                for (Material material : Material.values()) {
                    if (material.isLegacy()) continue;
                    session.put(stat.name() + "_" + material.name() + "_start", player.getStatistic(stat, material));
                }
            }
        }
        CURRENT_SESSIONS.put(player.getUniqueId(), session);
        CoreUtils.logger().log("Session", "Started session for " + player.getName());
    }

    public static void finishSession(Player player) {
        if (!config.sessions.has(player.getUniqueId().toString()))
            config.sessions.put(player.getUniqueId().toString(), new JSONArray());
        JSONObject session = getSession(player);
        session.put("LEFT", System.currentTimeMillis());
        session.put("PLAYTIME", session.getLong("LEFT") - session.getLong("JOINED"));
        session.put("NAME", player.getName());
        for (Statistic stat : Statistic.values()) {
            if (stat.getType() == Statistic.Type.UNTYPED) {
                int previous = session.has(stat.name()) ? session.getInt(stat.name()) : 0;
                int recent = player.getStatistic(stat) - (session.has(stat.name() + "_start") ? session.getInt(stat.name() + "_start") : 0);
                int total = previous + recent;
                session.remove(stat.name() + "_start");
                if (total > 0) session.put(stat.name(), previous + recent);
            }
            if (stat.getType().equals(Statistic.Type.ENTITY)) {
                for (EntityType type : EntityType.values()) {
                    if (type.equals(EntityType.UNKNOWN)) continue;
                    int previous = session.has(stat.name() + "_" + type.name()) ? session.getInt(stat.name() + "_" + type.name()) : 0;
                    int recent = player.getStatistic(stat, type) - (session.has(stat.name() + "_" + type.name() + "_start") ? session.getInt(stat.name() + "_" + type.name() + "_start") : 0);
                    int total = previous + recent;
                    if (total > 0) {
                        if (!session.has(stat.name())) session.put(stat.name(), new JSONObject());
                        session.getJSONObject(stat.name()).put(type.name(), total);
                    }
                    session.remove(stat.name() + "_" + type.name() + "_start");
                }
            }
            if (stat.getType().equals(Statistic.Type.BLOCK) || stat.getType().equals(Statistic.Type.ITEM)) {
                for (Material material : Material.values()) {
                    if (material.isLegacy()) continue;
                    int previous = session.has(stat.name() + "_" + material.name()) ? session.getInt(stat.name() + "_" + material.name()) : 0;
                    int recent = player.getStatistic(stat, material) - (session.has(stat.name() + "_" + material.name() + "_start") ? session.getInt(stat.name() + "_" + material.name() + "_start") : 0);
                    int total = previous + recent;
                    if (total != 0) {
                        if (!session.has(stat.name())) session.put(stat.name(), new JSONObject());
                        session.getJSONObject(stat.name()).put(material.name(), total);
                    }
                    session.remove(stat.name() + "_" + material.name() + "_start");
                }
            }
        }
        config.sessions.getJSONArray(player.getUniqueId().toString()).put(session);
        CURRENT_SESSIONS.remove(player.getUniqueId());
        config.save();
        CoreUtils.logger().log("Session", "Finished session for " + player.getName());
    }

    public static long getOverallPlaytime(Player player) {
        long playtime = 0;
        JSONArray sessions = getSessions(player);
        for (int i = 0; i != sessions.length(); i++) {
            JSONObject session = sessions.getJSONObject(i);
            if (session.has("PLAYTIME")) playtime = playtime + session.getLong("PLAYTIME");
            else {
                playtime = playtime + (System.currentTimeMillis() - getSession(player).getLong("JOINED"));
            }
        }
        return playtime;
    }

    public static JSONArray getSessions(Player player) {
        final JSONArray sessions = new JSONArray();
        if (config.sessions.has(player.getUniqueId().toString())) {
            JSONArray old_ses = config.sessions.getJSONArray(player.getUniqueId().toString());
            for (int i = 0; i != old_ses.length(); i++)
                sessions.put(old_ses.getJSONObject(i));
        }
        sessions.put(getSession(player));
        return sessions;

    }
}

