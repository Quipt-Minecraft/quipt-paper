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
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class TeleportRequestAcceptCommand extends CommandExecutor {

    public TeleportRequestAcceptCommand(JavaPlugin plugin) {
        super(plugin, "teleportrequestaccept");
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> execute() {
        return literal(getName()).executes(context -> {
            if(!(context.getSource().getSender() instanceof Player player)) return logError(context, MessageUtils.getMessage("cmd.error.player_only"));
            for(LocationUtils.TeleportRequest request : LocationUtils.requests()){
                if(request.target().equals(player)){
                    request.accept();
                    return 1;
                }
            }
            return 1;
        }).build();
    }
}
