package me.quickscythe.quipt.tests.objects;


import me.quickscythe.quipt.listeners.EventListener;
import me.quickscythe.quipt.listeners.PlayerListener;
import me.quickscythe.quipt.listeners.SessionListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BukkitConverterIntegration extends JavaPlugin {

    @Override
    public void onEnable() {
//        CoreUtils.init(new QuiptPaperIntegration(this));
//
//
        new PlayerListener(this);
        new EventListener(this);
        new SessionListener(this);

    }





}
