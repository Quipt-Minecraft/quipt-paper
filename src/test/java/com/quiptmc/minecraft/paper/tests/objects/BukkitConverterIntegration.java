package com.quiptmc.minecraft.paper.tests.objects;


import com.quiptmc.minecraft.paper.listeners.EventListener;
import com.quiptmc.minecraft.paper.listeners.PlayerListener;
import com.quiptmc.minecraft.paper.listeners.SessionListener;
import org.bukkit.plugin.java.JavaPlugin;

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
