package me.quickscythe.quipt.utils;

import me.quickscythe.Bot;
import me.quickscythe.api.guild.QuiptGuild;
import me.quickscythe.api.guild.channel.QuiptTextChannel;
import me.quickscythe.api.plugins.BotPlugin;
import me.quickscythe.api.plugins.BotPluginLoader;
import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.discord.embed.Embed;
import me.quickscythe.quipt.files.DiscordConfig;
import me.quickscythe.quipt.files.HashesConfig;
import me.quickscythe.quipt.files.JenkinsConfig;
import me.quickscythe.quipt.files.ResourceConfig;
import me.quickscythe.quipt.listeners.quipt.QuiptPlayerListener;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.quipt.utils.events.EventManager;
import me.quickscythe.quipt.utils.heartbeat.HeartbeatUtils;
import me.quickscythe.quipt.utils.resources.ResourcePackServer;
import me.quickscythe.quipt.utils.sessions.SessionManager;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.Optional;

public class PaperIntegration extends QuiptIntegration {

    private final String name = "Quipt";
    private final File dataFolder;
    private final Optional<JavaPlugin> plugin;

    public PaperIntegration(@Nullable JavaPlugin plugin) {
        this.plugin = Optional.ofNullable(plugin);
        this.dataFolder = plugin == null ? new File("plugins/" + name()) : plugin.getDataFolder();
    }

    public void enable() {

        if (!dataFolder.exists()) log("Initializer", "Creating data folder: " + dataFolder.mkdir());
        HeartbeatUtils.init(this);
        events().register(new QuiptPlayerListener());



    }

    @Override
    public File dataFolder() {
        return dataFolder;
    }

    @Override
    public String name() {
        return name;
    }

    public Optional<JavaPlugin> plugin() {
        return plugin;
    }
}
