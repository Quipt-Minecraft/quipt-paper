package com.quiptmc.minecraft.paper.utils;

import com.quiptmc.core.entity.QuiptPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class QuiptConversionUtils {

    public static QuiptPlayer convertPlayer(OfflinePlayer player) {
        if(player == null) return null;
        return new QuiptPlayer(player.getName(), player.getUniqueId());
    }

    public static OfflinePlayer convertPlayer(QuiptPlayer player) {
        return Bukkit.getOfflinePlayer(player.uuid());
    }
}
