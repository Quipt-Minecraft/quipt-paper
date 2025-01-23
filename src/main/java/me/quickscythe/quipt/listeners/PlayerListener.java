package me.quickscythe.quipt.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.quickscythe.Bot;
import me.quickscythe.api.guild.QuiptGuild;
import me.quickscythe.api.guild.channel.QuiptTextChannel;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.entity.QuiptPlayer;
import me.quickscythe.quipt.api.events.QuiptPlayerChatEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerDeathEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerJoinEvent;
import me.quickscythe.quipt.api.events.QuiptPlayerLeaveEvent;
import me.quickscythe.quipt.files.DiscordConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.QuiptConversionUtils;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class PlayerListener implements Listener {
    public PlayerListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException, NoSuchAlgorithmException {
        QuiptPlayerJoinEvent joinEvent = new QuiptPlayerJoinEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), MessageUtils.plainText(e.joinMessage()));
        CoreUtils.quiptPlugin().events().handle(joinEvent);
        CoreUtils.packServer().setPack(e.getPlayer());

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        QuiptPlayerLeaveEvent joinEvent = new QuiptPlayerLeaveEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), MessageUtils.plainText(e.quitMessage()));
        CoreUtils.quiptPlugin().events().handle(joinEvent);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        QuiptPlayerDeathEvent deathEvent = new QuiptPlayerDeathEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), QuiptConversionUtils.convertPlayer(e.getPlayer().getKiller()), MessageUtils.plainText(e.deathMessage()));
        CoreUtils.quiptPlugin().events().handle(deathEvent);

    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        if (e.isCancelled()) return;
        QuiptPlayerChatEvent chatEvent = new QuiptPlayerChatEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), MessageUtils.plainText(e.message()));
        CoreUtils.quiptPlugin().events().handle(chatEvent);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

}
