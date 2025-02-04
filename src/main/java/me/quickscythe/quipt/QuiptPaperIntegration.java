package me.quickscythe.quipt;

import me.quickscythe.Bot;
import me.quickscythe.api.guild.QuiptGuild;
import me.quickscythe.api.guild.channel.QuiptTextChannel;
import me.quickscythe.api.plugins.BotPlugin;
import me.quickscythe.api.plugins.BotPluginLoader;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.discord.embed.Embed;
import me.quickscythe.quipt.files.*;
import me.quickscythe.quipt.utils.PaperIntegration;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.quipt.utils.events.EventManager;
import me.quickscythe.quipt.utils.resources.ResourcePackServer;
import me.quickscythe.quipt.utils.sessions.SessionManager;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;


public class QuiptPaperIntegration extends PaperIntegration {


    private  ResourcePackServer packserver;


    public QuiptPaperIntegration(@Nullable JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        super.enable();
        ResourceConfig resourceConfig = ConfigManager.registerConfig(this, ResourceConfig.class);
        ConfigManager.registerConfig(this, JenkinsConfig.class);
        ConfigManager.registerConfig(this, HashesConfig.class);
        DiscordConfig discordConfig = ConfigManager.registerConfig(this, DiscordConfig.class);
        SessionManager.start(this);
        EventManager.init();

        PlaceholderUtils.registerPlaceholders();
        MessageUtils.start();

        packserver = new ResourcePackServer(this);

        if (!resourceConfig.repo_url.isEmpty()) {
            packserver.setUrl(resourceConfig.repo_url);
        }

        try {
            LocationUtils.start(this);
        } catch (Exception e) {
            log("Initializer", "Failed to start location utils");
        }
        if (discordConfig.enable_bot && plugin().isPresent()) {
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
    }

    public ResourcePackServer packServer() {
        return packserver;
    }
}
