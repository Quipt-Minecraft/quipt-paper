package me.quickscythe.quipt.utils;


import me.quickscythe.quipt.QuiptPaperIntegration;
import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.ResourceConfig;
import me.quickscythe.quipt.utils.resources.ResourcePackServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CoreUtils {

    private static QuiptPaperIntegration integration;


    public static void preInit(){
        //DO NOT USE BUKKIT CODE HERE. ONLY REGISTER DATA UNRELATED TO PAPER AND QUIPT INTEGRATIONS
    }

    public static void init(QuiptPaperIntegration integration) {
        CoreUtils.integration = integration;

        integration.enable();


    }


    public static QuiptPaperIntegration integration() {
        return integration;
    }

    public static ResourcePackServer packServer() {
        return integration().packServer();
    }
}
