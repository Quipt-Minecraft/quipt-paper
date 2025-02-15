package com.quiptmc.minecraft.paper.listeners.quipt;

import com.quiptmc.discord.Bot;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.entity.QuiptPlayer;
import com.quiptmc.core.events.QuiptPlayerChatEvent;
import com.quiptmc.core.events.QuiptPlayerDeathEvent;
import com.quiptmc.core.events.QuiptPlayerJoinEvent;
import com.quiptmc.core.events.QuiptPlayerLeaveEvent;
import com.quiptmc.core.events.listeners.Listener;
import com.quiptmc.discord.api.guild.QuiptGuild;
import com.quiptmc.discord.api.guild.channel.QuiptTextChannel;
import com.quiptmc.minecraft.paper.files.DiscordConfig;
import com.quiptmc.minecraft.paper.utils.CoreUtils;
import org.bukkit.Bukkit;

public class QuiptPlayerListener implements Listener.QuiptPlayerJoinListener, Listener.QuiptPlayerLeaveListener, Listener.QuiptPlayerChatListener, Listener.QuiptPlayerDeathEventListener {


    public void onPlayerJoin(QuiptPlayerJoinEvent event) {
        QuiptPlayer player = event.player();
        String message = event.message();

        DiscordConfig config = ConfigManager.getConfig(CoreUtils.integration(), DiscordConfig.class);
        if (config.enable_bot && CoreUtils.integration().plugin().isPresent() && config.announcements.join) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.channels.player_status) || channel.getId().equalsIgnoreCase(config.channels.player_status)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.integration().plugin().get(), () -> channel.sendPlayerMessage(player.uuid(), player.name(), message), 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerChat(QuiptPlayerChatEvent e) {

        DiscordConfig config = ConfigManager.getConfig(CoreUtils.integration(), DiscordConfig.class);
        if (config.enable_bot && CoreUtils.integration().plugin().isPresent() && config.announcements.chat) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.channels.player_status) || channel.getId().equalsIgnoreCase(config.channels.player_status)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.integration().plugin().get(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(QuiptPlayerLeaveEvent e) {
        DiscordConfig config = ConfigManager.getConfig(CoreUtils.integration(), DiscordConfig.class);
        if (config.enable_bot && CoreUtils.integration().plugin().isPresent() && config.announcements.leave) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.channels.player_status) || channel.getId().equalsIgnoreCase(config.channels.player_status)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.integration().plugin().get(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerDeath(QuiptPlayerDeathEvent e) {
        DiscordConfig config = ConfigManager.getConfig(CoreUtils.integration(), DiscordConfig.class);
        if (config.enable_bot && CoreUtils.integration().plugin().isPresent() && config.announcements.death) {
            for (QuiptGuild guild : Bot.qda().getGuilds()) {
                for (QuiptTextChannel channel : guild.getTextChannels()) {
                    if (channel.getName().equalsIgnoreCase(config.channels.player_status) || channel.getId().equalsIgnoreCase(config.channels.player_status)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.integration().plugin().get(), () -> {
                            channel.sendPlayerMessage(e.player().uuid(), e.player().name(), e.message());
                        }, 0L);
                    }
                }
            }
        }
    }
}
