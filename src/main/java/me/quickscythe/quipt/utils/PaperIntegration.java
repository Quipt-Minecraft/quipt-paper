package me.quickscythe.quipt.utils;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.listeners.quipt.QuiptPlayerListener;
import me.quickscythe.quipt.utils.heartbeat.HeartbeatUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

public abstract class PaperIntegration extends QuiptIntegration {

    private final String name = "Quipt";
    private final File dataFolder;
    private final Optional<JavaPlugin> plugin;

    public PaperIntegration(@Nullable JavaPlugin plugin) {
        this.plugin = Optional.ofNullable(plugin);
        this.dataFolder = plugin == null ? new File("plugins/" + name()) : plugin.getDataFolder();
    }

    public void enable() {

        if (!dataFolder.exists()) log("Initializer", "Creating data folder: " + dataFolder.mkdir());
        HeartbeatUtils.init(this);
        events().register(new QuiptPlayerListener());


    }

    @Override
    public File dataFolder() {
        return dataFolder;
    }

    @Override
    public String name() {
        return name;
    }

    public Optional<JavaPlugin> plugin() {
        return plugin;
    }

    @Override
    public String version() {
        return plugin().isPresent() ? plugin().get().getDescription().getVersion() : "DEV";
    }


}
