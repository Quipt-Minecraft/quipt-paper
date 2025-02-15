package com.quiptmc.minecraft.paper.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import com.quiptmc.core.events.QuiptPlayerChatEvent;
import com.quiptmc.core.events.QuiptPlayerDeathEvent;
import com.quiptmc.core.events.QuiptPlayerJoinEvent;
import com.quiptmc.core.events.QuiptPlayerLeaveEvent;
import com.quiptmc.minecraft.paper.utils.CoreUtils;
import com.quiptmc.minecraft.paper.utils.QuiptConversionUtils;
import com.quiptmc.minecraft.paper.utils.chat.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        CoreUtils.integration().events().handle(joinEvent);
        if (CoreUtils.packHandler() != null) CoreUtils.packHandler().setPack(e.getPlayer());

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        QuiptPlayerLeaveEvent joinEvent = new QuiptPlayerLeaveEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), MessageUtils.plainText(e.quitMessage()));
        CoreUtils.integration().events().handle(joinEvent);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        QuiptPlayerDeathEvent deathEvent = new QuiptPlayerDeathEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), QuiptConversionUtils.convertPlayer(e.getPlayer().getKiller()), MessageUtils.plainText(e.deathMessage()));
        CoreUtils.integration().events().handle(deathEvent);

    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        if (e.isCancelled()) return;
        QuiptPlayerChatEvent chatEvent = new QuiptPlayerChatEvent(QuiptConversionUtils.convertPlayer(e.getPlayer()), MessageUtils.plainText(e.message()));
        CoreUtils.integration().events().handle(chatEvent);
    }


}
