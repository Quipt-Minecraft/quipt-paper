package me.quickscythe.quipt.commands.executors;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.quickscythe.quipt.commands.CommandExecutor;
import me.quickscythe.quipt.commands.executors.arguments.AdvancementArgumentType;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.events.EventManager;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class EventCommand extends CommandExecutor {
    public EventCommand(JavaPlugin plugin) {
        super(plugin, "session");
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> execute() {
        return literal(getName()).executes(context -> {
            return 1;
        }).then(literal("start").executes(context -> {
            if (!context.getSource().getSender().hasPermission("quipt.session.start"))
                return logError(context, MessageUtils.getMessage("cmd.error.no_perm"));
            EventManager.event().start();
            context.getSource().getSender().sendMessage(MessageUtils.getMessage("cmd.session.start"));
            return 1;
        })).then(literal("end").executes(context -> {
            if (!context.getSource().getSender().hasPermission("quipt.session.end"))
                return logError(context, MessageUtils.getMessage("cmd.error.no_perm"));
            EventManager.event().end();
            context.getSource().getSender().sendMessage(MessageUtils.getMessage("cmd.session.end"));
            return 1;
        })).then(literal("reward").executes(context -> {
            //todo build item
            return showUsage(context, "quipt.session.reward");
        }).then(argument("reward", ArgumentTypes.itemStack()).executes(context -> {
            if (!context.getSource().getSender().hasPermission("quipt.session.reward"))
                return logError(context, MessageUtils.getMessage("cmd.error.no_perm"));
            ItemStack reward = context.getArgument("reward", ItemStack.class);
            EventManager.event().reward(reward);
            context.getSource().getSender().sendMessage(MessageUtils.getMessage("cmd.session.reward"));
            return 1;
        }))).then(literal("task").executes(context -> {
            //todo build task
            return showUsage(context, "quipt.session.task");
        }).then(argument("task", new AdvancementArgumentType()).executes(context -> {
            if (!context.getSource().getSender().hasPermission("quipt.session.task"))
                return logError(context, MessageUtils.getMessage("cmd.error.no_perm"));
            Advancement task = context.getArgument("task", Advancement.class);
            EventManager.event().task(task);
            context.getSource().getSender().sendMessage(MessageUtils.getMessage("cmd.session.task"));
            return 1;
        }))).build();
    }
}
