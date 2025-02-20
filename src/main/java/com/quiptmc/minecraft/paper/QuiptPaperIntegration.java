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
import com.quiptmc.minecraft.paper.utils.ApiManager;
import com.quiptmc.minecraft.paper.utils.PaperIntegration;
import com.quiptmc.minecraft.paper.utils.chat.MessageUtils;
import com.quiptmc.minecraft.paper.utils.chat.placeholder.PlaceholderUtils;
import com.quiptmc.minecraft.paper.utils.events.EventManager;
import com.quiptmc.minecraft.paper.utils.heartbeat.Flutter;
import com.quiptmc.minecraft.paper.utils.heartbeat.HeartbeatUtils;
import com.quiptmc.minecraft.paper.utils.sessions.SessionManager;
import com.quiptmc.minecraft.paper.utils.teleportation.LocationUtils;
import com.quiptmc.minecraft.paper.web.CallbackHandler;
import com.quiptmc.minecraft.paper.web.HealthReportHandler;
import com.quiptmc.minecraft.paper.web.ResourcePackHandler;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.discord.embed.Embed;
import com.quiptmc.core.server.QuiptServer;
import com.quiptmc.minecraft.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class QuiptPaperIntegration extends PaperIntegration {

    private ResourcePackHandler packHandler;
    private QuiptServer server;
    private MinecraftServer minecraftServer;
    private ApiManager apiManager;
    private CallbackHandler callbackHandler;

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
        apiManager = new ApiManager(this);

        ApiConfig apiConfig = ConfigManager.getConfig(this, ApiConfig.class);
        WebConfig webConfig = ConfigManager.getConfig(this, WebConfig.class);
        QuiptServer.ServerConfig serverConfig = new QuiptServer.ServerConfig(QuiptServer.ServerProtocol.HTTP, webConfig.host, webConfig.port);
        DiscordConfig discordConfig = ConfigManager.getConfig(this, DiscordConfig.class);
        ResourceConfig resourceConfig = ConfigManager.getConfig(this, ResourceConfig.class);

        server = new QuiptServer(this, serverConfig);
        JSONObject paperProperties = loadJsonFromYamlFile(new File("config/paper-global.yml"));

        if(!paperProperties.getJSONObject("proxies").getBoolean("proxy-protocol")){
            callbackHandler = new CallbackHandler(server);
            server.handler().handle("callback", callbackHandler, "callback/*");
        }

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
            public boolean run() {
                long now = System.currentTimeMillis();
                if (now - last >= TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)) {
                    apiManager.runUpdate();
                    last = now;
                }
                return true;
            }
        });

//        minecraftServer = new MinecraftServer(apiManager.getIP(), apiConfig.secret, apiConfig.endpoint + "/server_status/");
    }

    private JSONObject loadJsonFromYamlFile(File file) {
        try {
            Yaml yaml = new Yaml();
            return new JSONObject((Map<String, Object>) yaml.load(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MinecraftServer minecraftServer() {
        return minecraftServer;
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

    public ApiManager apiManager() {
        return apiManager;
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