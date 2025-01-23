package me.quickscythe.quipt.utils;

import me.quickscythe.quipt.api.entity.QuiptPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class QuiptConversionUtils {

    public static QuiptPlayer convertPlayer(OfflinePlayer player) {
        if(player == null) return null;
        return new QuiptPlayer(player.getName(), player.getUniqueId());
    }

    public static OfflinePlayer convertPlayer(QuiptPlayer player) {
        return Bukkit.getOfflinePlayer(player.uuid());
    }
}
