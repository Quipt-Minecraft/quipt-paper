package com.quiptmc.minecraft.paper;

import com.quiptmc.minecraft.paper.commands.CommandManager;
import com.quiptmc.minecraft.paper.listeners.EventListener;
import com.quiptmc.minecraft.paper.listeners.PlayerListener;
import com.quiptmc.minecraft.paper.listeners.SessionListener;
import com.quiptmc.minecraft.paper.utils.CoreUtils;
import com.quiptmc.core.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    @Override
    public void onEnable() {

        CoreUtils.init(new QuiptPaperIntegration(this));

        CommandManager.init(this);

        new PlayerListener(this);
        new EventListener(this);
        new SessionListener(this);

    }

    @Override
    public void onDisable() {
        ConfigManager.saveAll();
    }
}
