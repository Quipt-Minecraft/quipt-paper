/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt.commands.executors;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.quickscythe.quipt.commands.CommandExecutor;
import me.quickscythe.quipt.commands.executors.arguments.WarpArgumentType;
import me.quickscythe.quipt.utils.teleportation.points.TeleportationPoint;
import org.bukkit.plugin.java.JavaPlugin;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class WarpCommand extends CommandExecutor {
    public WarpCommand(JavaPlugin plugin) {
        super(plugin, "warp");
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> execute() {
        return literal(getName()).executes(context -> showUsage(context, "cmd.usage.warp")).then(literal("set").executes(context -> {
            context.getSource().getSender().sendMessage("Test");
            return 1;
        }).then(argument("warp", new WarpArgumentType()).executes(context -> {
            TeleportationPoint warp = context.getArgument("warp", TeleportationPoint.class);
            return 1;
        }))).then(literal("remove").executes(context -> {
            context.getSource().getSender().sendMessage("Test");
            return 1;
        }).then(argument("warp", new WarpArgumentType()).executes(context -> {
            TeleportationPoint warp = context.getArgument("warp", TeleportationPoint.class);
            return 1;
        }))).then(literal("list").executes(context -> {
            context.getSource().getSender().sendMessage("Test");
            return 1;
        })).then(argument("warp", new WarpArgumentType()).executes(context -> {
            TeleportationPoint warp = context.getArgument("warp", TeleportationPoint.class);
            return 1;
        })).build();
    }
}
