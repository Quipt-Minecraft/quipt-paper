package me.quickscythe.quipt.listeners.quipt;

import me.quickscythe.Bot;
import me.quickscythe.api.guild.QuiptGuild;
import me.quickscythe.api.guild.channel.QuiptTextChannel;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.entity.QuiptPlayer;
import me.quickscythe.quipt.api.events.QuiptPlayerChatEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerDeathEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerJoinEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerLeaveEvent;
import me.quickscythe.quipt.api.events.listeners.Listener;
import me.quickscythe.quipt.files.DiscordConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import org.bukkit.Bukkit;

public class QuiptPlayerListener implements Listener.QuiptPlayerJoinListener, Listener.QuiptPlayerLeaveListener, Listener.QuiptPlayerChatListener, Listener.QuiptPlayerDeathEventListener {


    public void onPlayerJoin(QuiptPlayerJoinEvent event) {
        QuiptPlayer player = event.player();
        String message = event.message();

        DiscordConfig config = ConfigManager.getConfig(CoreUtils.quiptPlugin(), DiscordConfig.class);
        if (config.enable_bot) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.player_status_channel) || channel.getId().equalsIgnoreCase(config.player_status_channel)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.plugin(), () -> channel.sendPlayerMessage(player.uuid(), player.name(), message), 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerChat(QuiptPlayerChatEvent e) {

        DiscordConfig config = ConfigManager.getConfig(CoreUtils.quiptPlugin(), DiscordConfig.class);
        if (config.enable_bot) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.chat_message_channel) || channel.getId().equalsIgnoreCase(config.chat_message_channel)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.plugin(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(QuiptPlayerLeaveEvent e) {
        DiscordConfig config = ConfigManager.getConfig(CoreUtils.quiptPlugin(), DiscordConfig.class);
        if (config.enable_bot) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.player_status_channel) || channel.getId().equalsIgnoreCase(config.player_status_channel)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.plugin(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerDeath(QuiptPlayerDeathEvent e) {
        DiscordConfig config = ConfigManager.getConfig(CoreUtils.quiptPlugin(), DiscordConfig.class);
        if (config.enable_bot) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.player_status_channel) || channel.getId().equalsIgnoreCase(config.player_status_channel)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.plugin(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }
}
