package me.quickscythe.quipt.commands.executors.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class AdvancementArgumentType implements CustomArgumentType.Converted<Advancement, String> {

    @Override
    public Advancement convert(@NotNull String nativeType) {
        return Bukkit.getServer().getAdvancement(NamespacedKey.fromString(nativeType));
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        for (@NotNull Iterator<Advancement> it = Bukkit.getServer().advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            builder.suggest("\"" + advancement.getKey().getKey() + "\"");
        }

        return builder.buildFuture();
    }
}