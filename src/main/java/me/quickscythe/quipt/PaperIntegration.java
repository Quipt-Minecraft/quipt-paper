package me.quickscythe.quipt;

import me.quickscythe.Bot;
import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.listeners.quipt.QuiptPlayerListener;
import me.quickscythe.quipt.utils.CoreUtils;

import java.io.File;

public class PaperIntegration extends QuiptIntegration {

    private final String name = "Quipt";
    private final File dataFolder;


    public PaperIntegration() {
        dataFolder = CoreUtils.plugin().getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
    }

    public void enable(){
        events().register(new QuiptPlayerListener());

    }

    @Override
    public void log(String tag, String message) {
        CoreUtils.logger().log(tag, message);
    }

    @Override
    public File dataFolder() {
        return dataFolder;
    }

    @Override
    public String name() {
        return name;
    }
}
