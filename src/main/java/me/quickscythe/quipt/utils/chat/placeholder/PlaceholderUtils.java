package me.quickscythe.quipt.utils.chat.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlaceholderUtils {

    static Map<String, PlaceholderWorker> placeholders = new HashMap<>();

    public static void registerPlaceholders() {
        registerPlaceholder("name", Player::getName);
        registerPlaceholder("player", Player::getName);
        registerPlaceholder("online", (player) -> Bukkit.getOnlinePlayers().size() + "");


    }

    public static void registerPlaceholder(String key, PlaceholderWorker worker) {
        placeholders.put("%" + key + "%", worker);
    }

    public static Component replace(Player player, Component component) {
        return component.replaceText(builder -> {
            for(Entry<String, PlaceholderWorker> e : placeholders.entrySet()){
                builder.match(e.getKey()).replacement(e.getValue().run(player));
            }
        });
    }

    public static String replace(Player player, String string) {

        for (Entry<String, PlaceholderWorker> e : placeholders.entrySet()) {
            if (string.contains(e.getKey())) {
                string = string.replaceAll(e.getKey(), e.getValue().run(player));
            }
        }

        string = emotify(string);
        return string;
    }

    public static String emotify(String string) {
        String tag = string;
        while (tag.contains("%symbol:")) {
            String icon = tag.split("ymbol:")[1].split("%")[0];
            if (Symbols.valueOf(icon.toUpperCase()) == null) {
                tag = tag.replaceAll("%symbol:" + icon + "%", Symbols.UNKNOWN.toString());
            } else {
                tag = tag.replaceAll("%symbol:" + icon + "%", Symbols.valueOf(icon.toUpperCase()).toString());
            }
        }
        return tag;
    }

    public static String markup(Player player, String string) {
        String tag = string;
        while (tag.contains("%bold:")) {
            String icon = tag.split("old:")[1].split("%")[0];
            tag = tag.replaceAll("%bold:" + icon + "%", ChatColor.BOLD + icon + ChatColor.getLastColors(tag.split("%bold")[0]));
        }
        while (tag.contains("%upper:")) {
            String icon = tag.split("pper:")[1].split("%")[0];
            tag = tag.replaceAll("%upper:" + icon + "%", icon.contains("%") ? replace(player, icon).toUpperCase() : icon.toUpperCase());
        }
        return tag;
    }

    @FunctionalInterface
    public interface PlaceholderWorker {

        public abstract String run(Player player);

    }

}