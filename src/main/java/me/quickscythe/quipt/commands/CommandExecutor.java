package me.quickscythe.quipt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public abstract class CommandExecutor {
    private final JavaPlugin plugin;
    private final String cmd;

    public CommandExecutor(JavaPlugin plugin, String cmd) {
        this.plugin = plugin;
        this.cmd = cmd;
    }

    public String getName() {
        return cmd;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public abstract LiteralCommandNode<CommandSourceStack> execute();

    public int logError(CommandContext<CommandSourceStack> context, String message) {
        return logError(context.getSource().getSender(), message);
    }

    public int logError(CommandContext<CommandSourceStack> context, Component message) {
        return logError(context.getSource().getSender(), message);
    }

    public int logError(CommandSender sender, String message) {
        return logError(sender, text(message));
    }

    public int logError(CommandSender sender, Component message) {
        sender.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

    public int showUsage(CommandSender sender, String perm) {
        return logError(sender, (perm.equalsIgnoreCase("") || sender.hasPermission(perm)) ? MessageUtils.getMessage("cmd." + getName() + ".usage") : MessageUtils.getMessage("cmd.error.no_perm", perm));

    }

    public int showUsage(CommandContext<CommandSourceStack> context, String perm) {
        return showUsage(context.getSource().getSender(), perm);
    }

    public static class Builder {
        CommandExecutor cmd;
        String desc = "";
        String[] aliases = new String[]{};


        @CheckReturnValue
        public Builder(CommandExecutor executor) {
            this.cmd = executor;
        }

        @CheckReturnValue
        public Builder setDescription(String desc) {
            this.desc = desc;
            return this;
        }

        @CheckReturnValue
        public Builder setAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public void register() {
            @NotNull LifecycleEventManager<Plugin> manager = cmd.getPlugin().getLifecycleManager();
            manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands commands = event.registrar();
                commands.register(cmd.execute(), desc, List.of(aliases));
            });
        }
    }
}
