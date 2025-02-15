package com.quiptmc.minecraft.paper;

import com.quiptmc.discord.Bot;
import com.quiptmc.discord.api.guild.QuiptGuild;
import com.quiptmc.discord.api.guild.channel.QuiptTextChannel;
import com.quiptmc.discord.api.plugins.BotPlugin;
import com.quiptmc.discord.api.plugins.BotPluginLoader;
import com.quiptmc.minecraft.paper.files.*;
import com.quiptmc.minecraft.paper.files.discord.AnnouncementsNestedConfig;
import com.quiptmc.minecraft.paper.files.discord.ChannelsNestedConfig;
import com.quiptmc.minecraft.paper.files.resource.AuthNestedConfig;
import com.quiptmc.minecraft.paper.files.resource.HashesNestedConfig;
import com.quiptmc.minecraft.paper.files.web.HealthReportNestedConfig;
import com.quiptmc.minecraft.paper.utils.PaperIntegration;
import com.quiptmc.minecraft.paper.utils.chat.MessageUtils;
import com.quiptmc.minecraft.paper.utils.chat.placeholder.PlaceholderUtils;
import com.quiptmc.minecraft.paper.utils.events.EventManager;
import com.quiptmc.minecraft.paper.utils.heartbeat.Flutter;
import com.quiptmc.minecraft.paper.utils.heartbeat.HeartbeatUtils;
import com.quiptmc.minecraft.paper.utils.sessions.SessionManager;
import com.quiptmc.minecraft.paper.utils.teleportation.LocationUtils;
import com.quiptmc.minecraft.paper.web.HealthReportHandler;
import com.quiptmc.minecraft.paper.web.ResourcePackHandler;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.discord.embed.Embed;
import com.quiptmc.core.server.QuiptServer;
import com.quiptmc.core.utils.NetworkUtils;
import com.quiptmc.minecraft.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class QuiptPaperIntegration extends PaperIntegration {

    private ResourcePackHandler packHandler;
    private QuiptServer server;
    private MinecraftServer minecraftServer;

    public QuiptPaperIntegration(@Nullable JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        super.enable();
        registerConfigs();
        SessionManager.start(this);
        EventManager.init();
        PlaceholderUtils.registerPlaceholders();
        MessageUtils.start();

        ApiConfig apiConfig = ConfigManager.getConfig(this, ApiConfig.class);
        WebConfig webConfig = ConfigManager.getConfig(this, WebConfig.class);
        QuiptServer.ServerConfig serverConfig = new QuiptServer.ServerConfig(QuiptServer.ServerProtocol.HTTP, webConfig.host, webConfig.port);
        DiscordConfig discordConfig = ConfigManager.getConfig(this, DiscordConfig.class);
        ResourceConfig resourceConfig = ConfigManager.getConfig(this, ResourceConfig.class);

        server = new QuiptServer(this, serverConfig);

        if (!resourceConfig.repo_url.isEmpty()) {
            packHandler = new ResourcePackHandler(server);
            server.handler().handle("resources", packHandler, "resources/*");
            packHandler.setUrl(resourceConfig.repo_url);
        }

        if (discordConfig.enable_bot && plugin().isPresent()) launchBot(discordConfig);

        if (webConfig.enable && webConfig.healthreport.enable)
            server.handler().handle("healthreport", new HealthReportHandler(server), "healthreport/*");

        try {
            LocationUtils.start(this);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HeartbeatUtils.heartbeat(this).addFlutter(new Flutter() {
            private final long started = System.currentTimeMillis();
            private long last = 0;

            @Override
            public boolean run() throws UnknownHostException {
                long now = System.currentTimeMillis();
                if (now - last >= TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)) {
                    last = now;
                    JSONObject data = new JSONObject();
                    data.put("players", Bukkit.getOnlinePlayers().size());
                    data.put("uptime", now - started);
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
                    data.put("player_stats", new JSONArray());

                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
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
                        data.getJSONArray("player_stats").put(playerData);
                    }
                    NetworkUtils.post(apiConfig.endpoint + "/update/" + getIP(), data);
                }
                return true;
            }
        });

        minecraftServer = new MinecraftServer(getIP(), apiConfig.secret, apiConfig.endpoint);
    }

    public MinecraftServer minecraftServer() {
        return minecraftServer;
    }

    private String getIP() {
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
        return "";
    }

    @SuppressWarnings("unchecked")
    private void registerConfigs() {
        ResourceConfig resourceConfig = ConfigManager.registerConfig(this, ResourceConfig.class);
        resourceConfig.auth = ConfigManager.getNestedConfig(resourceConfig, AuthNestedConfig.class, "auth");
        resourceConfig.hashes = ConfigManager.getNestedConfig(resourceConfig, HashesNestedConfig.class, "hashes");
        resourceConfig.save();
        ConfigManager.registerConfig(this, JenkinsConfig.class);
        DiscordConfig discordConfig = ConfigManager.registerConfig(this, DiscordConfig.class);
        discordConfig.announcements = ConfigManager.getNestedConfig(discordConfig, AnnouncementsNestedConfig.class, "announcements");
        discordConfig.channels = ConfigManager.getNestedConfig(discordConfig, ChannelsNestedConfig.class, "channels");
        WebConfig webConfig = ConfigManager.registerConfig(this, WebConfig.class);
        webConfig.healthreport = ConfigManager.getNestedConfig(webConfig, HealthReportNestedConfig.class, "healthreport");
        webConfig.save();
        ConfigManager.registerConfig(this, MessagesConfig.class);
        ConfigManager.registerConfig(this, ApiConfig.class);
    }

    private void launchBot(DiscordConfig discordConfig) {
        logger().log("Initializer", "Starting discord bot");
        if (plugin().isPresent())
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin().get(), () -> asyncBotLaunchThread(discordConfig), 20);
    }

    private void asyncBotLaunchThread(DiscordConfig discordConfig) {
        Bot.start(discordConfig.json());
        File folder = new File(dataFolder(), "discord_bots");
        if (!folder.exists()) folder.mkdir();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                BotPluginLoader loader = new BotPluginLoader();
                BotPlugin botPlugin = loader.registerPlugin(file);
                loader.enablePlugin(botPlugin);
            }
        }
        for (QuiptGuild guild : Bot.qda().getGuilds()) {
            for (QuiptTextChannel channel : guild.getTextChannels()) {
                if (channel.getName().equalsIgnoreCase(discordConfig.channels.server_status) || channel.getId().equalsIgnoreCase(discordConfig.channels.server_status)) {
                    Embed embed = new Embed();
                    embed.title("Server Status");
                    embed.description("Server has started.");
                    embed.color(Color.GREEN.getRGB());
                    channel.sendMessage(embed);
                }
            }
        }
    }

    public ResourcePackHandler packHandler() {
        return packHandler;
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}