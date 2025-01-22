package me.quickscythe.quipt;

import me.quickscythe.quipt.api.QuiptPlugin;
import me.quickscythe.quipt.utils.CoreUtils;

import java.io.File;

public class Plugin implements QuiptPlugin {

    private final String name = "Quipt";
    private final File dataFolder;


    public Plugin() {
        dataFolder = CoreUtils.plugin().getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
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
