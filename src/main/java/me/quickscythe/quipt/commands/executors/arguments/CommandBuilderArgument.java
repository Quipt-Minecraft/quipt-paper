package me.quickscythe.quipt.commands.executors.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class CommandBuilderArgument implements CustomArgumentType.Converted<CommandBuilderArgument.ExecutableCommand, String> {


    @Override
    public ExecutableCommand convert(@NotNull String nativeType) {
        String[] args = nativeType.split(" ");
        String label = args[0];
        System.out.println(label);

        return new ExecutableCommand(Bukkit.getCommandMap().getCommand(label), args);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getInput();

        String[] args = input.split(" ");
        //remove args[0] and args[1] from args
        if (args.length <= 3) {
            for (String command : Bukkit.getCommandMap().getKnownCommands().keySet()) {
                builder.suggest(command);
            }
            return builder.buildFuture();
        }
        String[] args1 = new String[args.length - 3];
        System.arraycopy(args, 3, args1, 0, args1.length);
        String label = args[2];
        Bukkit.broadcastMessage("Label: " + label + " Args: " + Arrays.toString(args1));

        CommandSender sender = ((CommandSourceStack) context.getSource()).getSender();
        for (String command : Bukkit.getCommandMap().getKnownCommands().keySet()) {
            if (command.startsWith(input)) {
                Bukkit.broadcastMessage("Command: " + command);
                for (String suggestion : Bukkit.getCommandMap().getCommand(command).tabComplete(sender, label, args)) {
                    builder.suggest(suggestion);
                }
            }
        }
        return builder.buildFuture();
    }

    public record ExecutableCommand(Command command, String[] args) {
        public void execute(CommandSender sender) {
            String label = args[0].substring(1);
            //remove args[0] from args
            String[] args = new String[this.args.length - 1];
            System.arraycopy(this.args, 1, args, 0, args.length);

            command.execute(sender, label, args);
        }
    }
}
