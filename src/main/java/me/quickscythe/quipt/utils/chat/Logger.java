package me.quickscythe.quipt.utils.chat;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import static net.kyori.adventure.text.Component.text;

public class Logger {

    private final ComponentLogger LOG;

    public Logger(Plugin plugin) {
        LOG = plugin.getComponentLogger();
    }

    public void log(String tag, String msg) {
        log(LogLevel.INFO, tag, msg);
    }

    public void log(LogLevel level, String tag, String msg) {
        log(level, tag, msg, null);
    }

    public void log(LogLevel level, String tag, Component msg) {
        log(level, tag, msg, null);
    }

    public void log(LogLevel level, String tag, Exception ex) {
        StringBuilder trace = new StringBuilder();
        for (StackTraceElement el : ex.getStackTrace())
            trace.append(el).append("\n");
        log(level, tag, ex.getMessage() + ": " + trace, null);
    }

    public void log(LogLevel level, String tag, String msg, CommandSender feedback) {
        log(level, tag, Component.text(msg), feedback);
    }

    public void log(LogLevel level, String tag, Component msg, CommandSender feedback) {
        level = level == null ? LogLevel.INFO : level;
        switch (level) {
            case WARN -> LOG.warn("[{}] {}", tag, msg);
            case DEBUG -> LOG.debug("[{}] {}", tag, msg);
            case ERROR -> LOG.error("[{}] {}", tag, msg);
            case TRACE -> LOG.trace("[{}] {}", tag, msg);
            default -> LOG.info("[{}] {}", tag, msg);
        }
        if (feedback != null) feedback.sendMessage(level.getTag().append(text(" [" + tag + "] ")).append(msg));
    }

    public void error(String tag, Exception ex) {
        log(LogLevel.ERROR, tag, ex);
    }

    public ComponentLogger logger() {
        return LOG;
    }


    public enum LogLevel {
        INFO("[INFO]", "#438df2"), WARN("[WARN]", NamedTextColor.YELLOW), ERROR("[ERROR]", NamedTextColor.RED), TRACE("[TRACE]"), DEBUG("[DEBUG]");

        final String tag;
        final TextColor color;

        LogLevel(String tag, String color) {
            this.tag = tag;
            this.color = TextColor.fromCSSHexString(color);
        }

        LogLevel(String tag, NamedTextColor color) {
            this.tag = tag;
            this.color = color;
        }

        LogLevel(String tag) {
            this.tag = tag;
            this.color = NamedTextColor.GRAY;
        }

        public Component getTag() {
            return text().content("").color(NamedTextColor.WHITE).append(text(tag, color)).build();
        }

        public String getTagString() {
            return tag;
        }
    }

}
