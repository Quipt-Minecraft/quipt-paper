package me.quickscythe.quipt.commands.executors;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.quickscythe.quipt.commands.CommandExecutor;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;


public class ResourcePackCommand extends CommandExecutor {

    public ResourcePackCommand(JavaPlugin plugin) {
        super(plugin, "resourcepack");
    }

    // /rp update [url]

    @Override
    public LiteralCommandNode<CommandSourceStack> execute() {
        return literal(getName()).executes(context -> showUsage(context, "quipt.admin.resourcepack")).then(literal("update").executes(context -> {
            CoreUtils.packServer().updatePack();
            return 1;
        })).then(literal("reload").executes(context -> {
            CommandSender sender = context.getSource().getSender();

            if (!(sender instanceof Player player))
                return logError(sender, MessageUtils.getMessage("cmd.error.player_only"));
            try {
                CoreUtils.packServer().setPack(player);
                player.sendMessage(text("Resource pack reloaded.").color(NamedTextColor.GREEN));
            } catch (IOException | NoSuchAlgorithmException e) {
                CoreUtils.logger().error("ResourcePackCommand", e);
                sender.sendMessage(text("An error occurred while reloading the resource pack. Check the console for details.").color(NamedTextColor.RED));
            }
            return 1;
        })).build();
    }
}
