package com.quiptmc.minecraft.paper.commands;

import com.quiptmc.minecraft.paper.commands.executors.*;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {
    public static void init(JavaPlugin plugin) {

        new CommandExecutor.Builder(new ResourcePackCommand(plugin)).setDescription("Command to manage Resource Pack").setAliases("rp", "resource", "pack").register();
        new CommandExecutor.Builder(new TeleportRequestCommand(plugin)).setDescription("Request to teleport to a player").setAliases("tpa", "rtp", "tpr").register();
        new CommandExecutor.Builder(new TeleportRequestAcceptCommand(plugin)).setDescription("Accept a teleport request").setAliases("tpaccept", "tpac", "atp").register();
        new CommandExecutor.Builder(new WarpCommand(plugin)).setDescription("Warp command.").register();
        new CommandExecutor.Builder(new EventCommand(plugin)).setDescription("Default event command.").register();
        new CommandExecutor.Builder(new SudoCommand(plugin)).setDescription("Sudo command").register();
        new CommandExecutor.Builder(new QuiptEntityCommand(plugin)).setDescription("Quipt Entity Command").setAliases("qe", "qentity", "qen", "cer").register();
    }


}
