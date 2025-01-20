/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt.commands.executors.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import me.quickscythe.quipt.utils.teleportation.points.TeleportationPoint;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class WarpArgumentType implements CustomArgumentType.Converted<TeleportationPoint, String> {

    @Override
    public TeleportationPoint convert(@NotNull String nativeType) {
        return LocationUtils.get(nativeType);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        for(TeleportationPoint warp : LocationUtils.getAll()){
            if(warp.type().equals(TeleportationPoint.Type.WARP)) builder.suggest(warp.name());
        }

        return builder.buildFuture();
    }
}