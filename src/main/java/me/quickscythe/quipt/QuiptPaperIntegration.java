package me.quickscythe.quipt;

import me.quickscythe.Bot;
import me.quickscythe.api.guild.QuiptGuild;
import me.quickscythe.api.guild.channel.QuiptTextChannel;
import me.quickscythe.api.plugins.BotPlugin;
import me.quickscythe.api.plugins.BotPluginLoader;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.discord.embed.Embed;
import me.quickscythe.quipt.api.server.QuiptServer;
import me.quickscythe.quipt.files.*;
import me.quickscythe.quipt.files.resource.AuthNestedConfig;
import me.quickscythe.quipt.files.resource.HashesNestedConfig;
import me.quickscythe.quipt.files.web.HealthReportNestedConfig;
import me.quickscythe.quipt.utils.PaperIntegration;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.quipt.utils.events.EventManager;
import me.quickscythe.quipt.utils.sessions.SessionManager;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import me.quickscythe.quipt.web.handlers.HealthReportHandler;
import me.quickscythe.quipt.web.handlers.ResourcePackHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class QuiptPaperIntegration extends PaperIntegration {


    private ResourcePackHandler packHandler = null;

    private QuiptServer server = null;


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

        WebConfig webConfig = ConfigManager.getConfig(this, WebConfig.class);
        QuiptServer.ServerConfig serverConfig = new QuiptServer.ServerConfig(QuiptServer.ServerProtocol.HTTP, webConfig.host, webConfig.port);
        DiscordConfig discordConfig = ConfigManager.getConfig(this, DiscordConfig.class);
        ResourceConfig resourceConfig = ConfigManager.getConfig(this, ResourceConfig.class);

        server = new QuiptServer(this, serverConfig);

        if (!resourceConfig.repo_url.isEmpty()) {
            packHandler = new ResourcePackHandler(server);
            server.handler().handle("resources", packHandler, "resources/*");
            packHandler().setUrl(resourceConfig.repo_url);
        }

        if (discordConfig.enable_bot && plugin().isPresent())
            launchBot(discordConfig);

        if (webConfig.enable && webConfig.healthreport.enable)
            server.handler().handle("healthreport", new HealthReportHandler(server), "healthreport/*");

        try {
            LocationUtils.start(this);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerConfigs() {
        ResourceConfig resourceConfig = ConfigManager.registerConfig(this, ResourceConfig.class);
        resourceConfig.auth = ConfigManager.getNestedConfig(resourceConfig, AuthNestedConfig.class, "auth");
        resourceConfig.hashes = ConfigManager.getNestedConfig(resourceConfig, HashesNestedConfig.class, "hashes");
        resourceConfig.save();
        ConfigManager.registerConfig(this, JenkinsConfig.class);
        ConfigManager.registerConfig(this, DiscordConfig.class);
        WebConfig webConfig = ConfigManager.registerConfig(this, WebConfig.class);
        webConfig.healthreport = ConfigManager.getNestedConfig(webConfig, HealthReportNestedConfig.class, "healthreport");
        webConfig.save();
        ConfigManager.registerConfig(this, MessagesConfig.class);
    }

    private void launchBot(DiscordConfig discordConfig) {
        logger().log("Initializer", "Starting discord bot");
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin().get(), () -> {
            Bot.start(discordConfig.json());
            File folder = new File(dataFolder(), "discord_bots");
            if (!folder.exists()) folder.mkdir();
            for (File file : folder.listFiles()) {
                System.out.println("Checking file: " + file.getName());
                if (file.getName().endsWith(".jar")) {
                    System.out.println("Loading plugin: " + file.getName());
                    BotPluginLoader loader = new BotPluginLoader();
                    BotPlugin botPlugin = loader.registerPlugin(file);
                    System.out.println("Enabling plugin: " + botPlugin.name());
                    loader.enablePlugin(botPlugin);
                    System.out.println("Plugin enabled: " + botPlugin.name());
                }
            }
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {

                    if (channel.getName().equalsIgnoreCase(discordConfig.server_status_channel) || channel.getId().equalsIgnoreCase(discordConfig.server_status_channel)) {
                        Embed embed = new Embed();
                        embed.title("Server Status");
                        embed.description("Server has started.");
                        embed.color(Color.GREEN.getRGB());
                        channel.sendMessage(embed);
                    }
                }
            }
        }, 20);
    }


    public ResourcePackHandler packHandler() {
        return packHandler;
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        if(server !=null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
