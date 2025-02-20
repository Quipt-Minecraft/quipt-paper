package com.quiptmc.minecraft.paper.utils;

import com.google.gson.JsonSyntaxException;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.utils.NetworkUtils;
import com.quiptmc.minecraft.paper.QuiptPaperIntegration;
import com.quiptmc.minecraft.paper.files.ApiConfig;
import com.quiptmc.minecraft.paper.files.WebConfig;
import com.quiptmc.minecraft.paper.utils.chat.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ApiManager {

    private final QuiptPaperIntegration integration;
    private final ApiConfig apiConfig;
    private final long started;

    public ApiManager(QuiptPaperIntegration integration) {
        this.integration = integration;
        this.apiConfig = ConfigManager.getConfig(integration, ApiConfig.class);
        this.started = System.currentTimeMillis();
    }

    public String getIP() {
        if (!Bukkit.getIp().isEmpty()) return Bukkit.getIp();
        try {
            URL url = URI.create("http://checkip.amazonaws.com").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "localhost";
    }

    public JSONObject getServerData() {
        JSONObject data = new JSONObject();
        data.put("players", Bukkit.getOnlinePlayers().size());
        data.put("uptime", System.currentTimeMillis() - started);
        data.put("tps", Bukkit.getTPS()[0]);
        data.put("max_players", Bukkit.getMaxPlayers());
        data.put("server_name", Bukkit.getServer().getName());
        data.put("server_version", Bukkit.getServer().getVersion());
        data.put("server_motd", MessageUtils.plainText(Bukkit.getServer().motd()));
        data.put("server_ip", getIP());
        data.put("server_port", Bukkit.getServer().getPort());
        data.put("server_online", Bukkit.getServer().getOnlineMode());
        data.put("server_whitelist", Bukkit.getServer().hasWhitelist());
        data.put("server_spawn_protection", Bukkit.getServer().getSpawnRadius());
        data.put("server_view_distance", Bukkit.getServer().getViewDistance());
        data.put("server_gamemode", Bukkit.getServer().getDefaultGameMode().name());
        data.put("server_worlds", Bukkit.getServer().getWorlds().size());

        JSONArray allPlayerStats = new JSONArray();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            allPlayerStats.put(getPlayerData(player));
        }
        data.put("player_stats", allPlayerStats);
        return data;
    }

    public JSONObject getPlayerData(OfflinePlayer player) {
        JSONObject playerData = new JSONObject();
        playerData.put("name", player.getName());
        playerData.put("uuid", player.getUniqueId().toString());
        playerData.put("first_played", player.getFirstPlayed());
        playerData.put("last_played", player.getLastSeen());
        playerData.put("banned", player.isBanned());
        playerData.put("whitelisted", player.isWhitelisted());
        playerData.put("online", player.isOnline());
        playerData.put("op", player.isOp());
        JSONObject stats = new JSONObject();

        for (Statistic stat : Statistic.values()) {
            if (stat.getType().equals(Statistic.Type.UNTYPED)) {
                int statAmount = player.getStatistic(stat);
                if (statAmount > 0) stats.put(stat.name(), statAmount);
            } else if (stat.getType().equals(Statistic.Type.ENTITY)) {
                for (EntityType type : EntityType.values()) {
                    if (!type.equals(EntityType.UNKNOWN)) {
                        int statAmount = player.getStatistic(stat, type);
                        if (statAmount > 0) stats.put(stat.name() + "_" + type.name(), statAmount);
                    }
                }
            } else if (stat.getType().equals(Statistic.Type.BLOCK) || stat.getType().equals(Statistic.Type.ITEM)) {
                for (Material material : Material.values()) {
                    if (!material.isLegacy()) {
                        int statAmount = player.getStatistic(stat, material);
                        if (statAmount > 0) stats.put(stat.name() + "_" + material.name(), statAmount);
                    }
                }
            }
        }
        playerData.put("stats", stats);
        return playerData;
    }


    public void runUpdate() {
        //todo move this to MinecraftServer in the Minecraft Utility module?
        JSONObject request = new JSONObject();
        request.put("server_data", getServerData());

        WebConfig webConfig = ConfigManager.getConfig(integration, WebConfig.class);

        JSONObject secret = new JSONObject();
        secret.put("secret", apiConfig.secret);
        secret.put("callback_url", "http://" + getIP() + ":" + webConfig.port + "/callback");
//        secret.put("webook_port", apiConfig.webhook_port);
        request.put("secret", secret);
        try {
            ApiResponse update = api(apiConfig.endpoint + "/update/" + getIP(), request);
            integration.log("ApiHandler", "Sending update to API...");
            if (update.result() == ApiResponse.RequestResult.NO_ACTION) {
                integration.log("ApiHandler", "IP is not registered. No action was taken.");

                ApiResponse register = api(apiConfig.endpoint + "/register/" + getIP(), request);
                integration.log("ApiHandler", "Registering now...");
                if (register.result() == ApiResponse.RequestResult.SUCCESS) {
                    integration.log("ApiHandler", "Success. Attempting to resend update.");
                    integration.log("ApiHandler", api(apiConfig.endpoint + "/update/" + getIP(), request).result.equals(ApiResponse.RequestResult.SUCCESS) ? "Sent." : "Failed to send.");
                    return;
                }
                integration.log("ApiHandler", "There was an error registering this IP: \n" + register.raw.toString(2));
                return;
            }
            if (update.result.equals(ApiResponse.RequestResult.SUCCESS)) {
                integration.log("ApiHandler", "Sent.");
                return;
            }
            if (update.result.equals(ApiResponse.RequestResult.FAILURE)) {
                integration.log("ApiHandler", "There was an error updating this IP: \n" + update.raw.toString(2));
            }

        } catch (JSONException e) {
            throw new JsonSyntaxException("Unable to parse API response.");
        }

    }

    public ApiResponse api(String url, JSONObject data) throws JSONException {
        JSONObject raw = new JSONObject(NetworkUtils.post(url, data));
        return new ApiResponse(raw.optEnum(ApiResponse.RequestResult.class, "result", ApiResponse.RequestResult.NO_ACTION), raw);
    }

    public record ApiResponse(RequestResult result, JSONObject raw) {

        public enum RequestResult {
            SUCCESS, FAILURE, NO_ACTION
        }

    }

}
