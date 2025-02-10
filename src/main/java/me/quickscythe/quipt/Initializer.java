package me.quickscythe.quipt;

import io.papermc.paper.registry.RegistryKey;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.registries.Registries;
import me.quickscythe.quipt.api.registries.Registry;
import me.quickscythe.quipt.commands.CommandManager;
import me.quickscythe.quipt.listeners.EventListener;
import me.quickscythe.quipt.listeners.PlayerListener;
import me.quickscythe.quipt.listeners.SessionListener;
import me.quickscythe.quipt.utils.CoreUtils;
import net.kyori.adventure.text.Component;
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
