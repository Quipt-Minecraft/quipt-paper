package me.quickscythe.quipt.commands;

import me.quickscythe.quipt.commands.executors.*;
import me.quickscythe.quipt.utils.CoreUtils;

public class CommandManager {
    public static void init() {

        new CommandExecutor.Builder(new ResourcePackCommand(CoreUtils.plugin())).setDescription("Command to manage Resource Pack").setAliases("rp", "resource", "pack").register();
        new CommandExecutor.Builder(new TeleportRequestCommand(CoreUtils.plugin())).setDescription("Request to teleport to a player").setAliases("tpa", "rtp", "tpr").register();
        new CommandExecutor.Builder(new TeleportRequestAcceptCommand(CoreUtils.plugin())).setDescription("Accept a teleport request").setAliases("tpaccept", "tpac", "atp").register();
        new CommandExecutor.Builder(new WarpCommand(CoreUtils.plugin())).setDescription("Warp command").setDescription("Warp command.").register();
        new CommandExecutor.Builder(new EventCommand(CoreUtils.plugin())).setDescription("Event command").setDescription("Default event command.").register();
    }


}
